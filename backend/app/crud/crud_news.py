from typing import Any, List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.base import CRUDBase
from app.models.news import News
from app.schemas.news import NewsCreate, NewsUpdate


class CRUDNews(CRUDBase[News, NewsCreate, NewsUpdate]):

    # --- Переопределение методов get и remove для использования news_id (BigInteger) ---

    async def get(self, db: AsyncSession, id: Any) -> Optional[News]:
        """Получить новость по news_id."""
        statement = select(self.model).where(self.model.news_id == int(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[News]:
        """Удалить новость по news_id."""
        statement = select(self.model).where(self.model.news_id == int(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj

    # --- Дополнительные методы --- 

    async def get_multi_sorted_by_date(
        self, db: AsyncSession, *, skip: int = 0, limit: int = 100
    ) -> List[News]:
        """Получить список новостей, отсортированный по дате публикации (сначала новые)."""
        statement = (
            select(self.model)
            .order_by(self.model.publication_date.desc())
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()


news = CRUDNews(News) 