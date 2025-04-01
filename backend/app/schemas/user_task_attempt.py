from pydantic import BaseModel
from typing import Optional
from datetime import datetime

# Base
class UserTaskAttemptBase(BaseModel):
    user_id: int
    task_id: str
    attempt_number: int = 1
    points_earned: int = 0
    is_correct: Optional[bool] = None
    time_spent: Optional[int] = None # в секундах
    answer_text: Optional[str] = None
    feedback: Optional[str] = None

# Create (При создании попытки обычно не указывают feedback или points_earned)
class UserTaskAttemptCreate(BaseModel):
    user_id: int
    task_id: str
    # attempt_number обычно вычисляется на сервере
    time_spent: Optional[int] = None
    answer_text: Optional[str] = None

# Update (Например, для добавления оценки/фидбека)
class UserTaskAttemptUpdate(BaseModel):
    points_earned: Optional[int] = None
    is_correct: Optional[bool] = None
    feedback: Optional[str] = None

# Read
class UserTaskAttemptRead(UserTaskAttemptBase):
    attempt_id: int
    answer_timestamp: datetime
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True 