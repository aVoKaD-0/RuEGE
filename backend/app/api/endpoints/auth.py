from datetime import timedelta

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.api import deps
from app.core import security
from app.core.config import settings

router = APIRouter()


@router.post("/login", response_model=schemas.Token)
async def login_for_access_token(
    db: AsyncSession = Depends(deps.get_db),
    form_data: OAuth2PasswordRequestForm = Depends() # Используем стандартную форму OAuth2
):
    """
    Аутентификация пользователя и возврат access и refresh токенов.
    Использует стандартную форму OAuth2PasswordRequestForm (поля username и password).
    """
    user = await crud.user.authenticate(
        db, username=form_data.username, password=form_data.password
    )
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        user.user_id, expires_delta=access_token_expires
    )
    refresh_token = security.create_refresh_token(user.user_id)

    # Сохраняем refresh_token в БД (или в другом надежном хранилище)
    # Важно: нужно добавить логику инвалидации refresh токенов при смене пароля/выходе
    await crud.user.update(db, db_obj=user, obj_in={"refresh_token": refresh_token})
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "token_type": "bearer",
    }

@router.post("/register", response_model=schemas.UserRead)
async def register_new_user(
    *, # Запрещает передачу параметров как позиционных
    db: AsyncSession = Depends(deps.get_db),
    user_in: schemas.UserCreate
):
    """
    Регистрация нового пользователя.
    """
    # Проверяем, не занят ли username
    existing_user = await crud.user.get_by_username(db, username=user_in.username)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username already registered",
        )
    # Проверяем, не занят ли email
    existing_user_email = await crud.user.get_by_email(db, email=user_in.email)
    if existing_user_email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email already registered",
        )
        
    user = await crud.user.create(db=db, obj_in=user_in)
    return user

@router.post("/refresh", response_model=schemas.Token)
async def refresh_access_token(
    *, 
    db: AsyncSession = Depends(deps.get_db),
    refresh_token_in_body: str # Ожидаем refresh_token в теле запроса
):
    """
    Обновление access токена с помощью refresh токена.
    """
    payload = security.decode_token(refresh_token_in_body)
    if payload is None or payload.get("sub") is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid refresh token",
            headers={"WWW-Authenticate": "Bearer"},
        )
        
    user_id = int(payload["sub"])
    user = await crud.user.get(db, id=user_id)

    # Проверяем, что пользователь существует и refresh_token совпадает с тем, что в БД
    if not user or user.refresh_token != refresh_token_in_body:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid refresh token or user not found",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # Генерируем новую пару токенов
    access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    new_access_token = security.create_access_token(
        user.user_id, expires_delta=access_token_expires
    )
    new_refresh_token = security.create_refresh_token(user.user_id)

    # Обновляем refresh_token в БД
    await crud.user.update(db, db_obj=user, obj_in={"refresh_token": new_refresh_token})

    return {
        "access_token": new_access_token,
        "refresh_token": new_refresh_token,
        "token_type": "bearer",
    }

# Эндпоинт для получения информации о текущем пользователе (пример защищенного эндпоинта)
@router.get("/me", response_model=schemas.UserRead)
async def read_users_me(
    current_user: models.User = Depends(deps.get_current_active_user)
):
    """
    Получить информацию о текущем аутентифицированном пользователе.
    """
    return current_user
