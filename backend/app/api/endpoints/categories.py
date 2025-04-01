from typing import List

from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, schemas
from app.api import deps

router = APIRouter()


@router.get("/", response_model=List[schemas.CategoryRead])
async def read_categories(
    db: AsyncSession = Depends(deps.get_db),
    skip: int = Query(0, ge=0), # Параметр для пагинации
    limit: int = Query(100, ge=1, le=200), # Параметр для пагинации
    only_visible: bool = Query(True) # По умолчанию возвращаем только видимые
):
    """
    Получить список категорий.
    
    - **skip**: Количество категорий, которое нужно пропустить (для пагинации).
    - **limit**: Максимальное количество категорий для возврата (для пагинации).
    - **only_visible**: Возвращать только видимые категории (`is_visible = True`).
    """
    if only_visible:
        categories = await crud.category.get_multi_visible(db, skip=skip, limit=limit)
    else:
        # Если нужны все категории (включая скрытые), возможно, для админки
        # Потребуется зависимость для проверки прав доступа (например, get_current_active_superuser)
        # current_user: models.User = Depends(deps.get_current_active_superuser) 
        categories = await crud.category.get_multi(db, skip=skip, limit=limit)
    return categories

# Можно добавить эндпоинт для получения одной категории по ID, если нужно
# @router.get("/{category_id}", response_model=schemas.CategoryRead)
# async def read_category(
#     *, 
#     db: AsyncSession = Depends(deps.get_db),
#     category_id: str
# ):
#     category = await crud.category.get(db, id=category_id)
#     if not category:
#         raise HTTPException(status_code=404, detail="Category not found")
#     return category 