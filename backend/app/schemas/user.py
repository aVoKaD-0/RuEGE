from pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime

# --- Базовая схема --- 
# Общие поля, присутствующие во всех других схемах User
class UserBase(BaseModel):
    username: str
    email: EmailStr # Pydantic проверит, что это валидный email
    avatar_url: Optional[str] = None

# --- Схема для создания --- 
# Поля, необходимые для создания нового пользователя (получаем от клиента)
class UserCreate(UserBase):
    password: str # Получаем пароль при создании

# --- Схема для обновления --- 
# Поля, которые можно обновить (получаем от клиента)
class UserUpdate(BaseModel):
    username: Optional[str] = None
    email: Optional[EmailStr] = None
    avatar_url: Optional[str] = None
    password: Optional[str] = None # Позволяем обновлять пароль

# --- Схема для чтения --- 
# Поля, которые возвращаются клиенту (не включаем пароль)
class UserRead(UserBase):
    user_id: int
    created_at: datetime
    updated_at: datetime
    last_login: Optional[datetime] = None

    class Config:
        from_attributes = True # Позволяет Pydantic читать данные из атрибутов объекта (модели SQLAlchemy)


# --- Схема для чтения из БД (внутренняя) --- 
# Включает хеш пароля, не должна возвращаться клиенту
class UserInDB(UserRead):
    password_hash: str 