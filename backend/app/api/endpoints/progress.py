from typing import List
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.api import deps

router = APIRouter()


@router.get("/me", response_model=List[schemas.ProgressRead])
async def read_my_progress(
    db: AsyncSession = Depends(deps.get_db),
    skip: int = Query(0, ge=0),
    limit: int = Query(100, ge=1, le=200),
    current_user: models.User = Depends(deps.get_current_active_user)
):
    """
    Получить список прогресса текущего пользователя по контенту.
    
    - Требует аутентификации.
    - Поддерживает пагинацию.
    """
    user_progress = await crud.progress.get_multi_by_user(
        db, user_id=current_user.user_id, skip=skip, limit=limit
    )
    return user_progress

@router.post("/me/{content_id}", response_model=schemas.ProgressRead)
async def update_my_progress(
    *,
    db: AsyncSession = Depends(deps.get_db),
    content_id: str,
    progress_in: schemas.ProgressUpdate, # Данные для обновления (percentage, completed, last_accessed)
    current_user: models.User = Depends(deps.get_current_active_user)
):
    """
    Обновить или создать запись прогресса для текущего пользователя по конкретному контенту.
    
    - Требует аутентификации.
    - Если запись прогресса для `content_id` не существует, она будет создана.
    - Автоматически обновляет `last_accessed`, если оно не передано.
    """
    # Проверяем, существует ли контент
    content_item = await crud.content.get(db, id=content_id)
    if not content_item:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Content not found")
        
    # Получаем или создаем запись прогресса
    progress_obj = await crud.progress.get_or_create(
        db, user_id=current_user.user_id, content_id=content_id
    )
    
    # Устанавливаем last_accessed, если не передано
    update_data = progress_in.model_dump(exclude_unset=True)
    if "last_accessed" not in update_data or update_data["last_accessed"] is None:
        update_data["last_accessed"] = datetime.now(datetime.timezone.utc)
        
    updated_progress = await crud.progress.update(
        db, db_obj=progress_obj, obj_in=update_data
    )
    return updated_progress 