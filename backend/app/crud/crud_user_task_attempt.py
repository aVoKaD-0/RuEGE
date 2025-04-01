from typing import Any, List, Optional

from sqlalchemy import select, func, and_
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.base import CRUDBase
from app.models.user_task_attempt import UserTaskAttempt
from app.schemas.user_task_attempt import UserTaskAttemptCreate, UserTaskAttemptUpdate


class CRUDUserTaskAttempt(CRUDBase[UserTaskAttempt, UserTaskAttemptCreate, UserTaskAttemptUpdate]):

    # --- Переопределение методов get и remove для использования attempt_id (BigInteger) ---

    async def get(self, db: AsyncSession, id: Any) -> Optional[UserTaskAttempt]:
        """Получить попытку по attempt_id."""
        statement = select(self.model).where(self.model.attempt_id == int(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[UserTaskAttempt]:
        """Удалить попытку по attempt_id."""
        statement = select(self.model).where(self.model.attempt_id == int(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj
        
    # --- Дополнительные методы --- 

    async def get_next_attempt_number(self, db: AsyncSession, *, user_id: int, task_id: str) -> int:
        """Получить следующий номер попытки для пользователя и задачи."""
        statement = (
            select(func.max(self.model.attempt_number))
            .where(
                and_(
                    self.model.user_id == user_id,
                    self.model.task_id == task_id
                )
            )
        )
        result = await db.execute(statement)
        max_attempt = result.scalar_one_or_none()
        return (max_attempt or 0) + 1

    async def create_attempt(self, db: AsyncSession, *, obj_in: UserTaskAttemptCreate, user_id: int) -> UserTaskAttempt:
        """Создать новую попытку с автоматическим номером попытки."""
        next_attempt_number = await self.get_next_attempt_number(
            db, user_id=user_id, task_id=obj_in.task_id
        )
        
        attempt_data = obj_in.model_dump()
        db_obj = self.model(
            **attempt_data,
            user_id=user_id, # Берем из аутентифицированного пользователя
            attempt_number=next_attempt_number
            # points_earned и is_correct будут установлены позже, если потребуется проверка
        )
        db.add(db_obj)
        await db.commit()
        await db.refresh(db_obj)
        return db_obj

    async def get_multi_by_user_and_task(
        self, db: AsyncSession, *, user_id: int, task_id: str, skip: int = 0, limit: int = 10
    ) -> List[UserTaskAttempt]:
        """Получить список попыток для пользователя и задачи."""
        statement = (
            select(self.model)
            .where(
                and_(
                    self.model.user_id == user_id,
                    self.model.task_id == task_id
                )
            )
            .order_by(self.model.attempt_number.desc()) # Сначала последние попытки
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()


user_task_attempt = CRUDUserTaskAttempt(UserTaskAttempt) 