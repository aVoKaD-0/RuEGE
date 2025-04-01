from typing import Any, List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload # Для эффективной загрузки связей

from app.crud.base import CRUDBase
from app.models.task import Task
from app.schemas.task import TaskCreate, TaskUpdate


class CRUDTask(CRUDBase[Task, TaskCreate, TaskUpdate]):

    # --- Переопределение методов get и remove для использования task_id (String) --- 

    async def get(self, db: AsyncSession, id: Any) -> Optional[Task]:
        """Получить задачу по task_id."""
        statement = select(self.model).where(self.model.task_id == str(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def get_with_options(self, db: AsyncSession, id: Any) -> Optional[Task]:
        """Получить задачу по task_id вместе с вариантами ответов."""
        statement = (
            select(self.model)
            .where(self.model.task_id == str(id))
            .options(selectinload(self.model.options)) # Загружаем связанные опции
        )
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[Task]:
        """Удалить задачу по task_id."""
        statement = select(self.model).where(self.model.task_id == str(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj
        
    # --- Дополнительные методы --- 

    async def get_multi_by_content(
        self, db: AsyncSession, *, content_id: str, skip: int = 0, limit: int = 100
    ) -> List[Task]:
        """Получить список задач для указанного контента."""
        statement = (
            select(self.model)
            .where(self.model.content_id == content_id)
            .order_by(self.model.task_id) # Или по другому полю, если нужно
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()


task = CRUDTask(Task) 