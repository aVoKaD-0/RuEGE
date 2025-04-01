-- Скрипт инициализации базы данных PostgreSQL

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Для хранения хеша пароля
    refresh_token VARCHAR(512),          -- Для refresh token
    avatar_url VARCHAR(512),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMPTZ
);

-- Таблица категорий
CREATE TABLE IF NOT EXISTS categories (
    category_id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url VARCHAR(512),
    order_position INTEGER DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Таблица контента (статьи, теория, шпаргалки и т.д.)
-- Добавляем возможность ссылаться на саму себя (parent_id)
CREATE TABLE IF NOT EXISTS contents (
    content_id VARCHAR(255) PRIMARY KEY,
    parent_id VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL, -- theory, task_group, cheatsheet, etc.
    is_downloadable BOOLEAN DEFAULT FALSE, -- Заменил is_downloaded на семантически более корректное
    is_new BOOLEAN DEFAULT FALSE,
    order_position INTEGER DEFAULT 0,
    content_url VARCHAR(512), -- Может быть ссылка на markdown, pdf, etc.
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    -- Внешний ключ на категории
    FOREIGN KEY (parent_id) REFERENCES categories (category_id) ON UPDATE CASCADE ON DELETE SET NULL
    -- Самоссылка для вложенности контента (если parent_id - это другой content_id)
    -- CONSTRAINT fk_contents_self FOREIGN KEY (parent_id) REFERENCES contents (content_id) ON UPDATE CASCADE ON DELETE SET NULL
    -- Примечание: Одновременное использование parent_id для категорий и для самоссылки может быть сложным.
    -- Возможно, стоит разделить на category_id и parent_content_id
);
CREATE INDEX IF NOT EXISTS idx_contents_parent_id ON contents(parent_id);
CREATE INDEX IF NOT EXISTS idx_contents_type ON contents(type);

-- Таблица новостей
CREATE TABLE IF NOT EXISTS news (
    news_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(512),
    publication_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Таблица прогресса пользователей по контенту
CREATE TABLE IF NOT EXISTS progress (
    progress_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id VARCHAR(255) NOT NULL,
    -- title VARCHAR(255), -- Title можно получать из contents, дублировать не стоит
    percentage INTEGER DEFAULT 0 CHECK (percentage >= 0 AND percentage <= 100),
    completed BOOLEAN DEFAULT FALSE,
    last_accessed TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES contents (content_id) ON DELETE CASCADE,
    UNIQUE (user_id, content_id) -- У пользователя может быть только одна запись прогресса для контента
);
CREATE INDEX IF NOT EXISTS idx_progress_user_id ON progress(user_id);
CREATE INDEX IF NOT EXISTS idx_progress_content_id ON progress(content_id);

-- Таблица заданий
CREATE TABLE IF NOT EXISTS tasks (
    task_id VARCHAR(255) PRIMARY KEY,
    content_id VARCHAR(255), -- Задание может быть частью какого-то контента (темы)
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty INTEGER DEFAULT 1 CHECK (difficulty BETWEEN 1 AND 5),
    max_points INTEGER DEFAULT 1,
    task_type VARCHAR(50) NOT NULL, -- test, coding, essay, match, etc.
    time_limit INTEGER DEFAULT 0, -- в секундах, 0 - нет лимита
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (content_id) REFERENCES contents (content_id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_tasks_content_id ON tasks(content_id);
CREATE INDEX IF NOT EXISTS idx_tasks_type ON tasks(task_type);

-- Таблица вариантов ответов для заданий (особенно типа 'test')
CREATE TABLE IF NOT EXISTS task_options (
    option_id BIGSERIAL PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    explanation TEXT,
    order_position INTEGER DEFAULT 0,
    FOREIGN KEY (task_id) REFERENCES tasks (task_id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_task_options_task_id ON task_options(task_id);

-- Таблица попыток выполнения заданий пользователями
CREATE TABLE IF NOT EXISTS user_task_attempts (
    attempt_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_id VARCHAR(255) NOT NULL,
    attempt_number INTEGER NOT NULL DEFAULT 1,
    points_earned INTEGER DEFAULT 0,
    is_correct BOOLEAN, -- Может быть NULL, если оценка не бинарная
    time_spent INTEGER, -- в секундах
    answer_text TEXT, -- Ответ пользователя (для эссе, кода или просто выбранные option_id)
    answer_timestamp TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    feedback TEXT, -- Обратная связь от системы/преподавателя
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks (task_id) ON DELETE CASCADE,
    UNIQUE (user_id, task_id, attempt_number)
);
CREATE INDEX IF NOT EXISTS idx_user_task_attempts_user_id ON user_task_attempts(user_id);
CREATE INDEX IF NOT EXISTS idx_user_task_attempts_task_id ON user_task_attempts(task_id);

-- Функции для автоматического обновления updated_at
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Применение триггеров к таблицам
DO $$
DECLARE
  tbl_name TEXT;
BEGIN
  FOR tbl_name IN 
    SELECT table_name 
    FROM information_schema.columns 
    WHERE table_schema = 'public' -- или ваша схема
      AND column_name = 'updated_at'
  LOOP
    EXECUTE format('DROP TRIGGER IF EXISTS set_timestamp ON %I;', tbl_name);
    EXECUTE format('CREATE TRIGGER set_timestamp
                    BEFORE UPDATE ON %I
                    FOR EACH ROW
                    EXECUTE FUNCTION trigger_set_timestamp();', tbl_name);
  END LOOP;
END;
$$;


