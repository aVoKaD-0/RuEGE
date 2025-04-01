from typing import Any, Dict, Optional, Union

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.security import get_password_hash, verify_password
from app.crud.base import CRUDBase
from app.models.user import User
from app.schemas.user import UserCreate, UserUpdate


class CRUDUser(CRUDBase[User, UserCreate, UserUpdate]):
    async def get_by_email(self, db: AsyncSession, *, email: str) -> Optional[User]:
        """Получить пользователя по email."""
        statement = select(self.model).where(self.model.email == email)
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def get_by_username(self, db: AsyncSession, *, username: str) -> Optional[User]:
        """Получить пользователя по имени пользователя."""
        statement = select(self.model).where(self.model.username == username)
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def create(self, db: AsyncSession, *, obj_in: UserCreate) -> User:
        """Создать нового пользователя с хешированием пароля."""
        # Преобразуем Pydantic схему в dict, исключая поле password
        create_data = obj_in.model_dump(exclude={"password"})
        # Создаем объект модели, пока без пароля
        db_obj = self.model(**create_data)
        # Устанавливаем хешированный пароль
        db_obj.password_hash = get_password_hash(obj_in.password)
        
        db.add(db_obj)
        await db.commit()
        await db.refresh(db_obj)
        return db_obj

    async def update(
        self, 
        db: AsyncSession, 
        *, 
        db_obj: User, 
        obj_in: Union[UserUpdate, Dict[str, Any]]
    ) -> User:
        """Обновить пользователя, с возможностью обновления пароля."""
        if isinstance(obj_in, dict):
            update_data = obj_in
        else:
            update_data = obj_in.model_dump(exclude_unset=True)
        
        # Если в данных для обновления есть пароль, хешируем его
        if "password" in update_data and update_data["password"]:
            hashed_password = get_password_hash(update_data["password"])
            del update_data["password"] # Удаляем оригинальный пароль из данных
            update_data["password_hash"] = hashed_password # Добавляем хеш
        elif "password" in update_data: # Если пароль передан как None или пустая строка
            del update_data["password"] # Просто удаляем его, не обновляя хеш

        # Вызываем родительский метод update с подготовленными данными
        return await super().update(db, db_obj=db_obj, obj_in=update_data)

    async def authenticate(
        self, db: AsyncSession, *, username: str, password: str
    ) -> Optional[User]:
        """Аутентифицировать пользователя по имени пользователя и паролю."""
        user = await self.get_by_username(db, username=username)
        if not user:
            return None
        if not verify_password(password, user.password_hash):
            return None
        return user
    
    # --- Переопределение методов get и remove для использования user_id --- 
    # CRUDBase ожидает PK с именем 'id', а у нас 'user_id'
    
    async def get(self, db: AsyncSession, id: Any) -> Optional[User]:
        """Получить пользователя по user_id."""
        statement = select(self.model).where(self.model.user_id == id)
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[User]:
        """Удалить пользователя по user_id."""
        statement = select(self.model).where(self.model.user_id == id)
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj


user = CRUDUser(User) 