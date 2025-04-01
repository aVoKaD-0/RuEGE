from pydantic import BaseModel
from typing import Optional

# Base
class TaskOptionBase(BaseModel):
    task_id: str
    text: str
    is_correct: bool = False
    explanation: Optional[str] = None
    order_position: int = 0

# Create
class TaskOptionCreate(TaskOptionBase):
    pass

# Update
class TaskOptionUpdate(BaseModel):
    text: Optional[str] = None
    is_correct: Optional[bool] = None
    explanation: Optional[str] = None
    order_position: Optional[int] = None

# Read
class TaskOptionRead(TaskOptionBase):
    option_id: int
    # Не включаем created_at/updated_at, т.к. их нет в модели TaskOption

    class Config:
        from_attributes = True 