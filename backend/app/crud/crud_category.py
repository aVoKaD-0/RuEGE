from typing import Any, List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.base import CRUDBase
from app.models.category import Category
from app.schemas.category import CategoryCreate, CategoryUpdate


class CRUDCategory(CRUDBase[Category, CategoryCreate, CategoryUpdate]):
    
    # --- Переопределение методов get и remove для использования category_id (String) --- 
    
    async def get(self, db: AsyncSession, id: Any) -> Optional[Category]:
        """Получить категорию по category_id."""
        statement = select(self.model).where(self.model.category_id == str(id))
        result = await db.execute(statement)
        return result.scalar_one_or_none()

    async def remove(self, db: AsyncSession, *, id: Any) -> Optional[Category]:
        """Удалить категорию по category_id."""
        statement = select(self.model).where(self.model.category_id == str(id))
        result = await db.execute(statement)
        obj = result.scalar_one_or_none()
        if obj:
            await db.delete(obj)
            await db.commit()
        return obj
        
    # --- Дополнительные методы --- 

    async def get_multi_visible(
        self, db: AsyncSession, *, skip: int = 0, limit: int = 100
    ) -> List[Category]:
        """Получить список видимых категорий с пагинацией."""
        statement = (
            select(self.model)
            .where(self.model.is_visible == True)
            .order_by(self.model.order_position) # Сортируем по order_position
            .offset(skip)
            .limit(limit)
        )
        result = await db.execute(statement)
        return result.scalars().all()


category = CRUDCategory(Category) 