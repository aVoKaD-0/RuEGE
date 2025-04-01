from pydantic import BaseModel
from typing import Optional
from datetime import datetime

# Base
class CategoryBase(BaseModel):
    category_id: str
    title: str
    description: Optional[str] = None
    icon_url: Optional[str] = None
    order_position: int = 0
    is_visible: bool = True

# Create (может совпадать с Base, если ID генерируется клиентом)
class CategoryCreate(CategoryBase):
    pass

# Update
class CategoryUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    icon_url: Optional[str] = None
    order_position: Optional[int] = None
    is_visible: Optional[bool] = None

# Read
class CategoryRead(CategoryBase):
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True 