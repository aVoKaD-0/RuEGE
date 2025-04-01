from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.api import deps

router = APIRouter()


@router.get("/", response_model=List[schemas.ContentRead])
async def read_content_list(
    db: AsyncSession = Depends(deps.get_db),
    category_id: Optional[str] = Query(None, description="Filter content by category ID"),
    skip: int = Query(0, ge=0),
    limit: int = Query(100, ge=1, le=200),
    # Можно добавить зависимость от текущего пользователя, если нужно проверять доступ
    # current_user: models.User = Depends(deps.get_current_active_user)
):
    """
    Получить список контента.
    
    - Можно фильтровать по **category_id**.
    - Поддерживает пагинацию через **skip** и **limit**.
    """
    if category_id:
        # Проверяем, существует ли категория (опционально, но полезно)
        category = await crud.category.get(db, id=category_id)
        if not category:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Category not found")
        
        contents = await crud.content.get_multi_by_category(
            db, category_id=category_id, skip=skip, limit=limit
        )
    else:
        # Если category_id не указан, возвращаем весь контент (возможно, это не нужно?)
        contents = await crud.content.get_multi(db, skip=skip, limit=limit)
    return contents


@router.get("/{content_id}", response_model=schemas.ContentRead)
async def read_content_item(
    *,
    db: AsyncSession = Depends(deps.get_db),
    content_id: str,
    current_user: models.User = Depends(deps.get_current_active_user) # Защищаем эндпоинт
):
    """
    Получить детали конкретного элемента контента по его ID.
    Требует аутентификации.
    """
    content = await crud.content.get(db, id=content_id)
    if not content:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Content not found")
        
    # Здесь можно добавить логику проверки прав доступа пользователя к этому контенту,
    # если это необходимо (например, если контент платный или доступен не всем).
        
    return content 