-- Users
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       username VARCHAR(20) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN', 'USER') NOT NULL,
                       created_at DATETIME(6),
                       updated_at DATETIME(6),
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_username (username),
                       UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

-- League
CREATE TABLE league (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        country VARCHAR(255),
                        PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Season
CREATE TABLE season (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        year_range VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_season_year_range (year_range)
) ENGINE=InnoDB;

-- Team
CREATE TABLE team (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL,
                      league_id BIGINT NOT NULL,
                      PRIMARY KEY (id),
                      CONSTRAINT fk_team_league FOREIGN KEY (league_id) REFERENCES league (id)
) ENGINE=InnoDB;

-- Player
CREATE TABLE player (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        nationality VARCHAR(255),
                        primary_position VARCHAR(255) NOT NULL,
                        photo_url VARCHAR(255),
                        PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Player Season Stat
CREATE TABLE player_season_stat (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    player_id BIGINT NOT NULL,
                                    team_id BIGINT NOT NULL,
                                    season_id BIGINT NOT NULL,
                                    goals INT NOT NULL DEFAULT 0,
                                    assists INT NOT NULL DEFAULT 0,
                                    appearances INT NOT NULL DEFAULT 0,
                                    PRIMARY KEY (id),
                                    UNIQUE KEY uk_pss_player_team_season (player_id, team_id, season_id),
                                    CONSTRAINT fk_pss_player FOREIGN KEY (player_id) REFERENCES player (id),
                                    CONSTRAINT fk_pss_team FOREIGN KEY (team_id) REFERENCES team (id),
                                    CONSTRAINT fk_pss_season FOREIGN KEY (season_id) REFERENCES season (id)
) ENGINE=InnoDB;

-- Formation
CREATE TABLE formation (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           name VARCHAR(255) NOT NULL,
                           position_layout JSON NOT NULL,
                           PRIMARY KEY (id),
                           UNIQUE KEY uk_formation_name (name)
) ENGINE=InnoDB;

-- Best Eleven
CREATE TABLE best_eleven (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             formation_id BIGINT NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             created_at DATETIME(6),
                             updated_at DATETIME(6),
                             PRIMARY KEY (id),
                             CONSTRAINT fk_best_eleven_user FOREIGN KEY (user_id) REFERENCES users (id),
                             CONSTRAINT fk_best_eleven_formation FOREIGN KEY (formation_id) REFERENCES formation (id)
) ENGINE=InnoDB;

-- Best Eleven Slot
CREATE TABLE best_eleven_slot (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  best_eleven_id BIGINT NOT NULL,
                                  position_code VARCHAR(255) NOT NULL,
                                  player_id BIGINT NOT NULL,
                                  PRIMARY KEY (id),
                                  CONSTRAINT fk_slot_best_eleven FOREIGN KEY (best_eleven_id) REFERENCES best_eleven (id),
                                  CONSTRAINT fk_slot_player FOREIGN KEY (player_id) REFERENCES player (id)
) ENGINE=InnoDB;

-- Best Eleven Review
CREATE TABLE best_eleven_review (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    best_eleven_id BIGINT NOT NULL,
                                    content TEXT NOT NULL,
                                    created_at DATETIME(6),
                                    updated_at DATETIME(6),
                                    PRIMARY KEY (id),
                                    CONSTRAINT fk_review_best_eleven FOREIGN KEY (best_eleven_id) REFERENCES best_eleven (id)
) ENGINE=InnoDB;