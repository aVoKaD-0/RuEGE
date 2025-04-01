from typing import Any, List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.base import CRUDBase
from app.models.content import Content
from app.schemas.content import ContentCreate, ContentUpdate


class CRUDContent(CRUDBase[Content, ContentCreate, ContentUpdate]):

    # --- Переопределение методов get и remove для использования content_id (String) --- 

    async def get(self, db: AsyncSession, id: Any) -> Optional[Content]:
        """Получить контент по content_id."""
        statement = select(self.model).where(self.model.content_id == str(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[Content]:
        """Удалить контент по content_id."""
        statement = select(self.model).where(self.model.content_id == str(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj
        
    # --- Дополнительные методы --- 

    async def get_multi_by_category(
        self, db: AsyncSession, *, category_id: str, skip: int = 0, limit: int = 100
    ) -> List[Content]:
        """Получить список контента для указанной категории."""
        statement = (
            select(self.model)
            .where(self.model.parent_id == category_id)
            .order_by(self.model.order_position)
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()
    
    # Можно добавить и другие методы фильтрации/поиска, если нужно


content = CRUDContent(Content) 