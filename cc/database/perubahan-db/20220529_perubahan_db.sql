-- add address (varchar) and phone (text) 
ALTER TABLE public.user ADD COLUMN address varchar;
ALTER TABLE public.user ADD COLUMN phone text;
DROP TABLE user_details;