from pydantic import BaseModel
from typing import Optional
from datetime import datetime

# Base
class ContentBase(BaseModel):
    content_id: str
    parent_id: Optional[str] = None
    title: str
    description: Optional[str] = None
    type: str
    is_downloadable: bool = False
    is_new: bool = False
    order_position: int = 0
    content_url: Optional[str] = None

# Create
class ContentCreate(ContentBase):
    pass

# Update
class ContentUpdate(BaseModel):
    parent_id: Optional[str] = None
    title: Optional[str] = None
    description: Optional[str] = None
    type: Optional[str] = None
    is_downloadable: Optional[bool] = None
    is_new: Optional[bool] = None
    order_position: Optional[int] = None
    content_url: Optional[str] = None

# Read
class ContentRead(ContentBase):
    created_at: datetime
    updated_at: datetime
    # Можно добавить связанные данные, если нужно
    # parent_category: Optional["CategoryRead"] = None 

    class Config:
        from_attributes = True

# Если будем возвращать связанные данные, нужно будет импортировать схемы
# from .category import CategoryRead
# ContentRead.model_rebuild() # Обновить ссылки на типы 