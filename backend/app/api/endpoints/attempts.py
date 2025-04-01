from typing import List

from fastapi import APIRouter, Depends, HTTPException, status, Body
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud, models, schemas
from app.api import deps

router = APIRouter()


@router.post("/", response_model=schemas.UserTaskAttemptRead)
async def create_user_task_attempt(
    *, 
    db: AsyncSession = Depends(deps.get_db),
    attempt_in: schemas.UserTaskAttemptCreate, # Получаем данные из тела запроса
    current_user: models.User = Depends(deps.get_current_active_user)
):
    """
    Создать новую попытку выполнения задачи для текущего пользователя.
    
    - Требует аутентификации.
    - Автоматически определяет номер попытки.
    - Ожидает `task_id`, `answer_text`, `time_spent` в теле запроса.
    """
    # Проверяем, существует ли задача
    task = await crud.task.get(db, id=attempt_in.task_id)
    if not task:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Task not found")
        
    # Передаем ID текущего пользователя в CRUD функцию
    attempt = await crud.user_task_attempt.create_attempt(
        db=db, obj_in=attempt_in, user_id=current_user.user_id
    )
    
    # TODO: Здесь можно добавить логику проверки ответа (attempt_in.answer_text)
    # и обновления полей is_correct, points_earned в созданной попытке (attempt).
    # Это зависит от типа задачи (task.task_type).
    
    return attempt

@router.get("/", response_model=List[schemas.UserTaskAttemptRead])
async def read_user_task_attempts(
    db: AsyncSession = Depends(deps.get_db),
    task_id: str = Query(..., description="Task ID to filter attempts by"),
    skip: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=50),
    current_user: models.User = Depends(deps.get_current_active_user)
):
    """
    Получить список попыток текущего пользователя для конкретной задачи.
    
    - Требует аутентификации.
    - Фильтрует по **task_id** (обязательный параметр).
    - Поддерживает пагинацию.
    """
    attempts = await crud.user_task_attempt.get_multi_by_user_and_task(
        db, user_id=current_user.user_id, task_id=task_id, skip=skip, limit=limit
    )
    return attempts 