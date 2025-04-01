from typing import Generator, Optional

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import jwt
from pydantic import ValidationError
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.core import security
from app.core.config import settings
from app.db.session import AsyncSessionFactory, get_db # Импортируем get_db

# Схема OAuth2 для получения токена из заголовка Authorization: Bearer <token>
reusable_oauth2 = OAuth2PasswordBearer(
    tokenUrl=f"/api/v1/auth/login" # Указываем URL эндпоинта для получения токена
)

async def get_current_user(
    db: AsyncSession = Depends(get_db),
    token: str = Depends(reusable_oauth2)
) -> models.User:
    """Зависимость для получения текущего пользователя из JWT токена."""
    try:
        payload = security.decode_token(token)
        if payload is None:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Could not validate credentials (payload issue)",
            )
        token_data = schemas.TokenPayload(**payload)
    except (jwt.JWTError, ValidationError):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Could not validate credentials (token/validation error)",
        )
    
    if token_data.sub is None:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Could not validate credentials (no subject)",
        )
        
    user = await crud.user.get(db, id=token_data.sub)
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return user

async def get_current_active_user(
    current_user: models.User = Depends(get_current_user),
) -> models.User:
    """Зависимость для получения текущего активного пользователя."""
    # Здесь можно добавить проверку, активен ли пользователь (например, is_active флаг в модели)
    # if not current_user.is_active:
    #     raise HTTPException(status_code=400, detail="Inactive user")
    return current_user

# Можно добавить зависимость для суперпользователя, если потребуется
# async def get_current_active_superuser(
#     current_user: models.User = Depends(get_current_active_user),
# ) -> models.User:
#     if not crud.user.is_superuser(current_user):
#         raise HTTPException(
#             status_code=403, detail="The user doesn't have enough privileges"
#         )
#     return current_user 