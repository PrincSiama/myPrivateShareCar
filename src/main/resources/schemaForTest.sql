drop table if exists users cascade;
drop table if exists cars cascade;
drop table if exists review cascade;
drop table if exists booking cascade;
drop table if exists passport cascade;
drop table if exists driver_license cascade;

														-- USERS
create table users (
id int generated by default as identity not null primary key,
firstname varchar(100) not null,
lastname varchar(100) not null,
email varchar(100) not null unique,
birthday date,
registration_date date
);

														-- CARS
create table cars (
id int generated by default as identity not null primary key,
brand varchar(100) not null,
model varchar(100) not null,
year_of_manufacture int not null,
color varchar(100),
document_number varchar(100) not null,
registration_number varchar(10) not null,
owner_id int not null references users(id) on delete cascade,
price_per_day int
);

														-- REVIEW
create table review (
id int generated by default as identity not null primary key,
car_id int not null references cars(id) on delete cascade,
user_id int not null references users(id) on delete cascade,
text varchar(5000) not null,
write_date date not null
);

                                                        -- BOOKING
create table booking (
id int generated by default as identity not null primary key,
user_id int not null references users(id) on delete cascade,
car_id int not null references cars(id) on delete cascade,
start_rent date not null,
duration int not null,
end_rent date not null,
booking_status varchar(10) not null
);

                                                       -- PASSPORT
create table passport (
id int primary key references users(id),
passport_series varchar(4),
passport_number varchar(6),
date_of_issue date,
issued_by varchar(500)
);

                                                        -- DRIVER_LICENSE
create table driver_license (
id int primary key references users(id),
driver_license_series varchar(5),
driver_license_number varchar(6),
date_of_issue date,
issued_by varchar(100)
);

