from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

# Импортируем схемы связанных моделей для вложенного возврата
from .task_option import TaskOptionRead

# Base
class TaskBase(BaseModel):
    task_id: str
    content_id: Optional[str] = None
    title: str
    description: Optional[str] = None
    difficulty: int = Field(default=1, ge=1, le=5)
    max_points: int = Field(default=1, ge=1)
    task_type: str
    time_limit: int = 0 # в секундах

# Create
class TaskCreate(TaskBase):
    pass

# Update
class TaskUpdate(BaseModel):
    content_id: Optional[str] = None
    title: Optional[str] = None
    description: Optional[str] = None
    difficulty: Optional[int] = Field(default=None, ge=1, le=5)
    max_points: Optional[int] = Field(default=None, ge=1)
    task_type: Optional[str] = None
    time_limit: Optional[int] = None

# Read
class TaskRead(TaskBase):
    created_at: datetime
    updated_at: datetime
    # Включаем связанные опции при чтении задачи
    options: List[TaskOptionRead] = []

    class Config:
        from_attributes = True
