from sqlalchemy import String, Integer, Boolean, TIMESTAMP, func, Text, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from datetime import datetime
from typing import Optional, TYPE_CHECKING

from app.db.base import Base

if TYPE_CHECKING:
    from .category import Category # Для type hinting связи
    # from .content import Content # Для self-referencing связи (если будет)


class Content(Base):
    __tablename__ = "contents"

    content_id: Mapped[str] = mapped_column(String(255), primary_key=True)
    # Внешний ключ к Category (parent_id ссылается на category_id)
    # Используем Optional[str], так как в init.sql ON DELETE SET NULL
    parent_id: Mapped[Optional[str]] = mapped_column(String(255), ForeignKey("categories.category_id", ondelete="SET NULL", onupdate="CASCADE"), nullable=True, index=True)
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    type: Mapped[str] = mapped_column(String(50), nullable=False, index=True)
    is_downloadable: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    is_new: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    order_position: Mapped[int] = mapped_column(Integer, default=0, nullable=False)
    content_url: Mapped[Optional[str]] = mapped_column(String(512), nullable=True)
    created_at: Mapped[datetime] = mapped_column(
        TIMESTAMP(timezone=True),
        server_default=func.now(),
        nullable=False
    )
    updated_at: Mapped[datetime] = mapped_column(
        TIMESTAMP(timezone=True),
        server_default=func.now(),
        onupdate=func.now(),
        nullable=False
    )

    # Определение связи с Category (многие Content к одной Category)
    # `back_populates` должно совпадать с именем связи в Category (если она будет)
    parent_category: Mapped[Optional["Category"]] = relationship("Category", back_populates="contents")

    # Если нужна самоссылающаяся связь (для вложенности контента)
    # parent_content_id: Mapped[Optional[str]] = mapped_column(String(255), ForeignKey("contents.content_id"), nullable=True)
    # parent: Mapped[Optional["Content"]] = relationship("Content", remote_side=[content_id], back_populates="children")
    # children: Mapped[list["Content"]] = relationship("Content", back_populates="parent")

    def __repr__(self) -> str:
        return f"<Content(content_id='{self.content_id}', title='{self.title}', type='{self.type}')>"

# Добавим обратную связь в Category, если она нужна
# В CategoryEntity.java нет явных связей, так что пока оставим так.
# Если нужно будет получать список контента для категории, нужно добавить:
# Category.contents: Mapped[list["Content"]] = relationship("Content", back_populates="parent_category") 