from sqlalchemy import String, BigInteger, Integer, Boolean, TIMESTAMP, func, ForeignKey, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship
from datetime import datetime
from typing import Optional, TYPE_CHECKING

from app.db.base import Base

if TYPE_CHECKING:
    from .user import User
    from .content import Content


class Progress(Base):
    __tablename__ = "progress"
    __table_args__ = (UniqueConstraint('user_id', 'content_id', name='uq_progress_user_content'),)

    progress_id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    user_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False, index=True)
    content_id: Mapped[str] = mapped_column(String(255), ForeignKey("contents.content_id", ondelete="CASCADE"), nullable=False, index=True)
    percentage: Mapped[int] = mapped_column(Integer, default=0, nullable=False)
    completed: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    last_accessed: Mapped[Optional[datetime]] = mapped_column(TIMESTAMP(timezone=True), nullable=True)
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

    # Связи
    user: Mapped["User"] = relationship()
    content: Mapped["Content"] = relationship()

    def __repr__(self) -> str:
        return f"<Progress(progress_id={self.progress_id}, user_id={self.user_id}, content_id='{self.content_id}', percentage={self.percentage})>" 