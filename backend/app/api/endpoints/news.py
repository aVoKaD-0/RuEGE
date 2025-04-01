from typing import List

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.api import deps

router = APIRouter()


@router.get("/", response_model=List[schemas.NewsRead])
async def read_news_list(
    db: AsyncSession = Depends(deps.get_db),
    skip: int = Query(0, ge=0), 
    limit: int = Query(20, ge=1, le=100), # Лимит поменьше для новостей
    # Этот эндпоинт может быть публичным, но оставим возможность защиты
    # current_user: Optional[models.User] = Depends(deps.get_current_user_optional)
):
    """
    Получить список новостей, отсортированный по дате публикации (сначала новые).
    
    - Поддерживает пагинацию через **skip** и **limit**.
    - Доступен без аутентификации (по умолчанию).
    """
    news_list = await crud.news.get_multi_sorted_by_date(db, skip=skip, limit=limit)
    return news_list


@router.get("/{news_id}", response_model=schemas.NewsRead)
async def read_news_item(
    *,
    db: AsyncSession = Depends(deps.get_db),
    news_id: int,
    # current_user: Optional[models.User] = Depends(deps.get_current_user_optional)
):
    """
    Получить детали конкретной новости по ее ID.
    Доступен без аутентификации (по умолчанию).
    """
    news_item = await crud.news.get(db, id=news_id)
    if not news_item:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="News item not found")
    return news_item

# Эндпоинты для создания/обновления/удаления новостей можно добавить позже,
# если потребуется (например, для админки). Потребуют защиты. 