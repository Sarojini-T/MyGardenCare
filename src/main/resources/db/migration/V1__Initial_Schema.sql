CREATE TABLE app_users(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    zipcode VARCHAR(255),
    role VARCHAR(255) DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE plants (
                        id BIGSERIAL PRIMARY KEY,
                        common_name VARCHAR(255) NOT NULL,
                        scientific_name VARCHAR(255) NOT NULL UNIQUE,
                        alternate_names TEXT,
                        light_requirement TEXT,
                        soil_type TEXT,
                        life_cycle TEXT,
                        water_requirement VARCHAR(255),
                        height_in_meters DOUBLE PRECISION,
                        width_in_meters DOUBLE PRECISION,
                        growth VARCHAR(255),
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_plants (
                             id BIGSERIAL PRIMARY KEY,
                             nickname VARCHAR(255) NOT NULL,
                             user_id BIGINT NOT NULL,
                             plant_id BIGINT NOT NULL,
                             plant_container VARCHAR(255) NOT NULL,
                             plant_location VARCHAR(255) NOT NULL,
                             container_size VARCHAR(255),
                             has_drainage BOOLEAN,
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,


                             CONSTRAINT fk_user_plants_user FOREIGN KEY (user_id) REFERENCES app_users (id) ON DELETE CASCADE,
                             CONSTRAINT fk_user_plants_plant FOREIGN KEY (plant_id) REFERENCES plants (id) ON DELETE CASCADE,


                             CONSTRAINT uk_user_plant_nickname UNIQUE (user_id, nickname)
);