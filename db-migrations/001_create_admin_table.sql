-- Create admin table for admin login
CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `admin_name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `ux_admin_name` (`admin_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert a default admin (password: 123456) - change in production
INSERT IGNORE INTO `admin` (admin_name, password) VALUES ('admin', '123456');


