from pydantic import BaseModel
from typing import Optional
from datetime import datetime

# Base
class NewsBase(BaseModel):
    title: str
    description: Optional[str] = None
    image_url: Optional[str] = None
    publication_date: datetime

# Create
class NewsCreate(NewsBase):
    pass

# Update
class NewsUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    image_url: Optional[str] = None
    publication_date: Optional[datetime] = None

# Read
class NewsRead(NewsBase):
    news_id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True 