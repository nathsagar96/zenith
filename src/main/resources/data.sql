-- This script populates the database with sample data for all entities

-- Insert Users
INSERT INTO users (id, username, email, password, first_name, last_name, bio, role, created_at, updated_at) VALUES
(1, 'admin_user', 'admin@example.com', '$2a$10$EblMK.cv4.0kOz.5bZzZze.5bZzZze.5bZzZze.5bZzZze.5bZzZ', 'Admin', 'User', 'Administrator account', 'ADMIN', NOW(), NOW()),
(2, 'john_doe', 'john@example.com', '$2a$10$EblMK.cv4.0kOz.5bZzZze.5bZzZze.5bZzZze.5bZzZze.5bZzZ', 'John', 'Doe', 'Software developer and tech enthusiast', 'USER', NOW(), NOW()),
(3, 'jane_smith', 'jane@example.com', '$2a$10$EblMK.cv4.0kOz.5bZzZze.5bZzZze.5bZzZze.5bZzZze.5bZzZ', 'Jane', 'Smith', 'Tech blogger and writer', 'USER', NOW(), NOW()),
(4, 'bob_jones', 'bob@example.com', '$2a$10$EblMK.cv4.0kOz.5bZzZze.5bZzZze.5bZzZze.5bZzZze.5bZzZ', 'Bob', 'Jones', 'DevOps engineer', 'USER', NOW(), NOW()),
(5, 'alice_wonder', 'alice@example.com', '$2a$10$EblMK.cv4.0kOz.5bZzZze.5bZzZze.5bZzZze.5bZzZze.5bZzZ', 'Alice', 'Wonder', 'AI researcher', 'USER', NOW(), NOW());

-- Insert Categories
INSERT INTO categories (id, name, created_at, updated_at) VALUES
(1, 'Technology', NOW(), NOW()),
(2, 'Programming', NOW(), NOW()),
(3, 'AI', NOW(), NOW()),
(4, 'DevOps', NOW(), NOW()),
(5, 'Cloud', NOW(), NOW());

-- Insert Tags
INSERT INTO tags (id, name, created_at, updated_at) VALUES
(1, 'Java', NOW(), NOW()),
(2, 'Spring Boot', NOW(), NOW()),
(3, 'Microservices', NOW(), NOW()),
(4, 'Kubernetes', NOW(), NOW()),
(5, 'AWS', NOW(), NOW()),
(6, 'Machine Learning', NOW(), NOW()),
(7, 'Python', NOW(), NOW()),
(8, 'Docker', NOW(), NOW());

-- Insert Posts
INSERT INTO posts (id, title, slug, content, status, author_id, created_at, updated_at) VALUES
(1, 'Getting Started with Spring Boot', 'getting-started-with-spring-boot', 'This is a guide to getting started with Spring Boot...', 'PUBLISHED', 2, NOW(), NOW()),
(2, 'Microservices Architecture', 'microservices-architecture', 'Exploring microservices architecture patterns...', 'PUBLISHED', 2, NOW(), NOW()),
(3, 'Kubernetes for Developers', 'kubernetes-for-developers', 'A beginner guide to Kubernetes...', 'PUBLISHED', 3, NOW(), NOW()),
(4, 'AI in Software Development', 'ai-in-software-development', 'How AI is changing software development...', 'PUBLISHED', 4, NOW(), NOW()),
(5, 'Cloud Native Applications', 'cloud-native-applications', 'Building cloud native applications...', 'DRAFT', 5, NOW(), NOW());

-- Associate Posts with Categories
INSERT INTO post_categories (post_id, category_id) VALUES
(1, 1), -- Getting Started with Spring Boot -> Technology
(1, 2), -- Getting Started with Spring Boot -> Programming
(2, 1), -- Microservices Architecture -> Technology
(2, 2), -- Microservices Architecture -> Programming
(3, 1), -- Kubernetes for Developers -> Technology
(3, 4), -- Kubernetes for Developers -> DevOps
(4, 1), -- AI in Software Development -> Technology
(4, 3), -- AI in Software Development -> AI
(5, 1), -- Cloud Native Applications -> Technology
(5, 4), -- Cloud Native Applications -> DevOps
(5, 5); -- Cloud Native Applications -> Cloud

-- Associate Posts with Tags
INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), -- Getting Started with Spring Boot -> Java
(1, 2), -- Getting Started with Spring Boot -> Spring Boot
(2, 2), -- Microservices Architecture -> Spring Boot
(2, 3), -- Microservices Architecture -> Microservices
(3, 4), -- Kubernetes for Developers -> Kubernetes
(3, 8), -- Kubernetes for Developers -> Docker
(4, 3), -- AI in Software Development -> Microservices
(4, 6), -- AI in Software Development -> Machine Learning
(5, 4), -- Cloud Native Applications -> Kubernetes
(5, 5), -- Cloud Native Applications -> AWS
(5, 8); -- Cloud Native Applications -> Docker

-- Insert Comments
INSERT INTO comments (id, content, status, post_id, author_id, created_at, updated_at) VALUES
(1, 'Great introduction to Spring Boot!', 'APPROVED', 1, 3, NOW(), NOW()),
(2, 'Very helpful guide, thanks!', 'APPROVED', 1, 4, NOW(), NOW()),
(3, 'Can you explain more about dependency injection?', 'PENDING', 1, 5, NOW(), NOW()),
(4, 'Excellent overview of microservices', 'APPROVED', 2, 3, NOW(), NOW()),
(5, 'How do you handle service discovery?', 'PENDING', 2, 5, NOW(), NOW()),
(6, 'Kubernetes is indeed powerful', 'APPROVED', 3, 2, NOW(), NOW()),
(7, 'Any tips for local Kubernetes development?', 'PENDING', 3, 4, NOW(), NOW()),
(8, 'Fascinating insights on AI', 'APPROVED', 4, 2, NOW(), NOW()),
(9, 'How does this impact junior developers?', 'PENDING', 4, 3, NOW(), NOW()),
(10, 'Looking forward to more on this topic', 'APPROVED', 5, 2, NOW(), NOW());
