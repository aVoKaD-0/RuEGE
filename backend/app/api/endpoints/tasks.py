from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.api import deps

router = APIRouter()


@router.get("/", response_model=List[schemas.TaskRead])
async def read_task_list(
    db: AsyncSession = Depends(deps.get_db),
    content_id: Optional[str] = Query(None, description="Filter tasks by content ID"),
    skip: int = Query(0, ge=0),
    limit: int = Query(100, ge=1, le=200),
    current_user: models.User = Depends(deps.get_current_active_user) # Защищаем
):
    """
    Получить список задач.
    
    - Можно фильтровать по **content_id**.
    - Поддерживает пагинацию через **skip** и **limit**.
    - Требует аутентификации.
    """
    if content_id:
        # Проверяем, существует ли контент (опционально)
        content_item = await crud.content.get(db, id=content_id)
        if not content_item:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Content not found")
            
        tasks = await crud.task.get_multi_by_content(
            db, content_id=content_id, skip=skip, limit=limit
        )
    else:
        # Возвращать все задачи без фильтра может быть небезопасно или не нужно
        # Оставим пока возможность, но ее можно убрать или добавить права доступа
        tasks = await crud.task.get_multi(db, skip=skip, limit=limit)
    return tasks


@router.get("/{task_id}", response_model=schemas.TaskRead)
async def read_task_item(
    *,
    db: AsyncSession = Depends(deps.get_db),
    task_id: str,
    current_user: models.User = Depends(deps.get_current_active_user) # Защищаем
):
    """
    Получить детали конкретной задачи по ее ID, включая варианты ответов.
    Требует аутентификации.
    """
    # Используем get_with_options для загрузки связей
    task = await crud.task.get_with_options(db, id=task_id)
    if not task:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Task not found")
        
    # Здесь также можно добавить проверку прав доступа, если необходимо
        
    return task

# Эндпоинты для создания/обновления/удаления задач и опций можно добавить позже,
# если потребуется (например, для админки). 