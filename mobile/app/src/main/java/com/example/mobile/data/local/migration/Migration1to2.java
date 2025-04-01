package com.example.mobile.data.local.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Класс миграции для обновления базы данных с версии 1 на версию 2.
 * Корректно обновляет существующие таблицы и создает недостающие.
 */
public class Migration1to2 extends Migration {
    
    public Migration1to2() {
        super(1, 2);
    }
    
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Проверяем, существует ли таблица categories с другой структурой и обновляем ее
        try {
            // Сначала пробуем узнать, есть ли таблица categories
            database.execSQL("SELECT * FROM `categories` LIMIT 0");
            
            // Если код выполнился без ошибок, значит таблица существует.
            // Проверяем, есть ли в ней колонка is_visible
            boolean hasIsVisible = false;
            boolean hasOrderPosition = false;
            try {
                database.execSQL("SELECT `is_visible` FROM `categories` LIMIT 0");
                hasIsVisible = true;
            } catch (Exception e) {
                // Колонка is_visible не существует
            }
            
            try {
                database.execSQL("SELECT `order_position` FROM `categories` LIMIT 0");
                hasOrderPosition = true;
            } catch (Exception e) {
                // Колонка order_position не существует
            }
            
            // Если нет колонки is_visible, добавляем ее
            if (!hasIsVisible) {
                database.execSQL("ALTER TABLE `categories` ADD COLUMN `is_visible` INTEGER NOT NULL DEFAULT 1");
            }
            
            // Если нет колонки order_position, но есть order_index, переименовываем
            if (!hasOrderPosition) {
                try {
                    database.execSQL("SELECT `order_index` FROM `categories` LIMIT 0");
                    // Если успешно, создаем временную таблицу и переносим данные
                    database.execSQL("CREATE TABLE `categories_new` (" +
                            "`category_id` TEXT PRIMARY KEY NOT NULL, " +
                            "`title` TEXT, " +
                            "`description` TEXT, " +
                            "`icon_url` TEXT, " +
                            "`order_position` INTEGER NOT NULL, " +
                            "`is_visible` INTEGER NOT NULL DEFAULT 1)");
                    
                    // Копируем данные
                    database.execSQL("INSERT INTO `categories_new` " +
                            "(`category_id`, `title`, `description`, `icon_url`, `order_position`, `is_visible`) " +
                            "SELECT `category_id`, `title`, `description`, `icon_url`, `order_index`, 1 FROM `categories`");
                    
                    // Удаляем старую таблицу
                    database.execSQL("DROP TABLE `categories`");
                    
                    // Переименовываем новую таблицу
                    database.execSQL("ALTER TABLE `categories_new` RENAME TO `categories`");
                } catch (Exception e) {
                    // Если order_index тоже нет, просто добавляем order_position
                    database.execSQL("ALTER TABLE `categories` ADD COLUMN `order_position` INTEGER NOT NULL DEFAULT 0");
                }
            }
        } catch (Exception e) {
            // Таблица не существует, создаем с нуля
            database.execSQL("CREATE TABLE IF NOT EXISTS `categories` (" +
                    "`category_id` TEXT PRIMARY KEY NOT NULL, " +
                    "`title` TEXT, " +
                    "`description` TEXT, " +
                    "`icon_url` TEXT, " +
                    "`order_position` INTEGER NOT NULL, " +
                    "`is_visible` INTEGER NOT NULL)");
        }
        
        // Проверяем, существует ли таблица contents с другой структурой и обновляем ее
        try {
            // Сначала пробуем узнать, есть ли таблица contents
            database.execSQL("SELECT * FROM `contents` LIMIT 0");
            
            // Добавляем здесь логику обновления существующей таблицы contents, если нужно
            // Так же как и с categories, можно проверить наличие нужных колонок и добавить их
            
        } catch (Exception e) {
            // Таблица не существует, создаем с нуля
            database.execSQL("CREATE TABLE IF NOT EXISTS `contents` (" +
                    "`content_id` TEXT PRIMARY KEY NOT NULL, " +
                    "`title` TEXT, " +
                    "`description` TEXT, " +
                    "`type` TEXT, " +
                    "`parent_id` TEXT, " +
                    "`is_downloaded` INTEGER NOT NULL, " +
                    "`is_new` INTEGER NOT NULL, " +
                    "`order_position` INTEGER NOT NULL, " +
                    "`content_url` TEXT, " +
                    "FOREIGN KEY(`parent_id`) REFERENCES `categories`(`category_id`) ON UPDATE CASCADE ON DELETE CASCADE)");
            
            // Создаем индекс для внешнего ключа
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_contents_parent_id` ON `contents` (`parent_id`)");
        }
        
        // Создаем таблицу users с необходимыми полями, если она не существует
        database.execSQL("CREATE TABLE IF NOT EXISTS `users` (" +
                "`user_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`username` TEXT, " +
                "`email` TEXT, " +
                "`avatar_url` TEXT, " +
                "`created_at` INTEGER NOT NULL, " +
                "`last_login` INTEGER NOT NULL)");
                
        // Создаем таблицу news, если она не существует
        database.execSQL("CREATE TABLE IF NOT EXISTS `news` (" +
                "`news_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`title` TEXT, " +
                "`publication_date` INTEGER NOT NULL, " +
                "`description` TEXT, " +
                "`image_url` TEXT)");
                
        // Удаляем устаревший скрипт создания таблицы content
        
        // Создаем таблицу progress, если она не существует
        database.execSQL("CREATE TABLE IF NOT EXISTS `progress` (" +
                "`progress_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`user_id` INTEGER NOT NULL, " +
                "`content_id` TEXT NOT NULL, " +
                "`progress_percent` INTEGER NOT NULL, " +
                "`is_completed` INTEGER NOT NULL, " +
                "`last_access` INTEGER NOT NULL, " +
                "FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`content_id`) REFERENCES `contents`(`content_id`) ON UPDATE NO ACTION ON DELETE CASCADE)");
                
        // Создаем таблицу tasks, если она не существует
        database.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (" +
                "`task_id` TEXT PRIMARY KEY NOT NULL, " +
                "`content_id` TEXT NOT NULL, " +
                "`title` TEXT, " +
                "`description` TEXT, " +
                "`type` TEXT, " +
                "`difficulty` INTEGER NOT NULL, " +
                "`points` INTEGER NOT NULL, " +
                "`order_index` INTEGER NOT NULL, " +
                "FOREIGN KEY(`content_id`) REFERENCES `contents`(`content_id`) ON UPDATE NO ACTION ON DELETE CASCADE)");
                
        // Создаем таблицу task_options, если она не существует
        database.execSQL("CREATE TABLE IF NOT EXISTS `task_options` (" +
                "`option_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`task_id` TEXT NOT NULL, " +
                "`text` TEXT, " +
                "`is_correct` INTEGER NOT NULL, " +
                "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`task_id`) ON UPDATE NO ACTION ON DELETE CASCADE)");
                
        // Создаем таблицу user_task_attempts, если она не существует
        database.execSQL("CREATE TABLE IF NOT EXISTS `user_task_attempts` (" +
                "`attempt_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`user_id` INTEGER NOT NULL, " +
                "`task_id` TEXT NOT NULL, " +
                "`is_correct` INTEGER NOT NULL, " +
                "`score` INTEGER NOT NULL, " +
                "`attempt_time` INTEGER NOT NULL, " +
                "FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`task_id`) ON UPDATE NO ACTION ON DELETE CASCADE)");
                
        // Создаем индексы для внешних ключей, если они не существуют
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_progress_user_id` ON `progress` (`user_id`)");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_progress_content_id` ON `progress` (`content_id`)");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_content_id` ON `tasks` (`content_id`)");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_task_options_task_id` ON `task_options` (`task_id`)");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_task_attempts_user_id` ON `user_task_attempts` (`user_id`)");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_task_attempts_task_id` ON `user_task_attempts` (`task_id`)");
    }
} 