from typing import Any, List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.base import CRUDBase
from app.models.task_option import TaskOption
from app.schemas.task_option import TaskOptionCreate, TaskOptionUpdate


class CRUDTaskOption(CRUDBase[TaskOption, TaskOptionCreate, TaskOptionUpdate]):

    # --- Переопределение методов get и remove для использования option_id (BigInteger) --- 
    # CRUDBase по умолчанию работает с PK 'id'. У нас 'option_id'.

    async def get(self, db: AsyncSession, id: Any) -> Optional[TaskOption]:
        """Получить вариант ответа по option_id."""
        statement = select(self.model).where(self.model.option_id == int(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[TaskOption]:
        """Удалить вариант ответа по option_id."""
        statement = select(self.model).where(self.model.option_id == int(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj

    # --- Дополнительные методы --- 

    async def get_multi_by_task(
        self, db: AsyncSession, *, task_id: str, skip: int = 0, limit: int = 1000 # Лимит больше, т.к. опций может быть много
    ) -> List[TaskOption]:
        """Получить список вариантов ответов для указанной задачи."""
        statement = (
            select(self.model)
            .where(self.model.task_id == task_id)
            .order_by(self.model.order_position) # Сортируем по order_position
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()


task_option = CRUDTaskOption(TaskOption) 