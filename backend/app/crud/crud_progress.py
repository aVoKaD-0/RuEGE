from typing import Any, List, Optional

from sqlalchemy import select, and_
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.base import CRUDBase
from app.models.progress import Progress
from app.schemas.progress import ProgressCreate, ProgressUpdate


class CRUDProgress(CRUDBase[Progress, ProgressCreate, ProgressUpdate]):

    # --- Переопределение методов get и remove для использования progress_id (BigInteger) ---

    async def get(self, db: AsyncSession, id: Any) -> Optional[Progress]:
        """Получить прогресс по progress_id."""
        statement = select(self.model).where(self.model.progress_id == int(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[Progress]:
        """Удалить прогресс по progress_id."""
        statement = select(self.model).where(self.model.progress_id == int(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj
        
    # --- Дополнительные методы --- 

    async def get_by_user_and_content(
        self, db: AsyncSession, *, user_id: int, content_id: str
    ) -> Optional[Progress]:
        """Получить запись прогресса для пользователя и контента."""
        statement = select(self.model).where(
            and_(
                self.model.user_id == user_id,
                self.model.content_id == content_id
            )
        )
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def get_or_create(
        self, db: AsyncSession, *, user_id: int, content_id: str
    ) -> Progress:
        """Получить или создать запись прогресса для пользователя и контента."""
        progress = await self.get_by_user_and_content(
            db, user_id=user_id, content_id=content_id
        )
        if progress:
            return progress
        
        # Если не найдено, создаем новую запись
        # Возможно, стоит получать title контента здесь или передавать его
        progress_in = ProgressCreate(user_id=user_id, content_id=content_id)
        return await self.create(db, obj_in=progress_in)

    async def get_multi_by_user(
        self, db: AsyncSession, *, user_id: int, skip: int = 0, limit: int = 100
    ) -> List[Progress]:
        """Получить список прогресса для пользователя."""
        statement = (
            select(self.model)
            .where(self.model.user_id == user_id)
            .order_by(self.model.last_accessed.desc().nullslast(), self.model.updated_at.desc()) # Сначала недавно использованные
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()


progress = CRUDProgress(Progress) 