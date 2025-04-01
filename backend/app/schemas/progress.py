from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime

# Base
class ProgressBase(BaseModel):
    user_id: int
    content_id: str
    percentage: int = Field(default=0, ge=0, le=100) # Добавим валидацию 0-100
    completed: bool = False
    last_accessed: Optional[datetime] = None

# Create
class ProgressCreate(ProgressBase):
    pass

# Update
class ProgressUpdate(BaseModel):
    percentage: Optional[int] = Field(default=None, ge=0, le=100)
    completed: Optional[bool] = None
    last_accessed: Optional[datetime] = None

# Read
class ProgressRead(ProgressBase):
    progress_id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True 