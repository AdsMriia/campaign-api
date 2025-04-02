--liquibase formatted sql

--changeset root:insert_into_bots
INSERT INTO bots (id, name, token, is_default)
VALUES ('d6f62e1c-fc12-4372-8080-5aeb56431df8', 'MRIYA_sub_bot', '6820029422:AAGfrXGhqzRDAs9tj25JXcPKI4YHOCB1cQg', true);

--changeset root:insert_into_plans
--comment Insert data into the plans table
INSERT INTO plans (id, name, max_admins, max_channels, price, currency, is_default)
VALUES  ('4d8aaac7-c04d-45d9-a0e3-b81550aceec9', 'GUEST', -1, -1, -1.0, 'USD', true),
        ('09ba693b-2695-4ca7-bde6-02a41e9c3e79', 'BASIC', 1, 1, 79.0, 'USD', true),
        ('555a6f05-7ffb-49cc-bbda-6e85dda5ecb8', 'MIDDLE', 3, 3, 129.0, 'USD', true),
        ('ba6e8456-8603-48e8-8bee-2df521eb8f0b', 'PREMIUM', 5, 5, 149.0, 'USD', true),
        ('ba6e8456-8603-48e8-8bee-2df531e48f0b', 'SUPER_ADMIN', -1, -1, -1.0, 'USD', false);

--changeset root:insert_into_otps
--comment Insert data into the otps table
INSERT INTO otp (id, otp, expiry_time, user_id, user_name, telegram_username)
VALUES  ('a7b2f3c1-2b6e-4eb1-80a9-7b3c5f31c1de', '123456789', TIMESTAMP '9999-12-31 23:59:59', '1', 'testUser', 'testUser');

--changeset root:insert_into_permissions
--comment Insert data into the permissions table
INSERT INTO permissions (id, name)
VALUES ('0086f63b-c75b-441a-9867-17b0fbfd7d60', 'MAIN_PAGE'),
       ('0186f63b-c75b-441a-9867-17b0fbfd7d60', 'SERVICE'),
--       ('0286f63b-c75b-441a-9867-17b0fbfd7d60', 'ABOUT_US'),
--       ('0386f63b-c75b-441a-9867-17b0fbfd7d60', 'SING_UP'),
--       ('0486f63b-c75b-441a-9867-17b0fbfd7d60', 'LOGIN'),
       ('0586f63b-c75b-441a-9867-17b0fbfd7d60', 'YOUR_MRIYA_BOT'),
       ('0686f63b-c75b-441a-9867-17b0fbfd7d60', 'MARIYA'),
       ('0786f63b-c75b-441a-9867-17b0fbfd7d60', 'EVA'),
       ('0886f63b-c75b-441a-9867-17b0fbfd7d60', 'WEB_STATS'),
       ('0986f63b-c75b-441a-9867-17b0fbfd7d60', 'POLL_BUILDER'),
       ('0a86f63b-c75b-441a-9867-17b0fbfd7d60', 'MESSAGE_BUILDER'),
       ('0b86f63b-c75b-441a-9867-17b0fbfd7d60', 'POLL_STATISTIC'),
       ('9b86f63b-c75b-441a-9867-17b0fbfd7d60', 'TARGET_AUDIENCE_PROFILE'),
       ('0c86f63b-c75b-441a-9867-17b0fbfd7d60', 'LOGS'),
       ('0d86f63b-c75b-441a-9867-17b0fbfd7d60', 'CHANNELS'),
       ('0e86f63b-c75b-441a-9867-17b0fbfd7d60', 'SUBSCRIPTION');

--changeset root:insert_into_plans_to_permissions
 --comment Insert data into plans_to_permissions table
 INSERT INTO plans_to_permissions (id, plan_id, permission_id)
 VALUES
-- -- GUEST MAIN_PAGE
--   ('f3b5a71a-10f4-4d35-9552-30244212ca07', '4d8aaac7-c04d-45d9-a0e3-b81550aceec9',
--      '0086f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- GUEST SERVICE
--     ('5a334d9a-166c-461b-9dd0-4378f3512f61', '4d8aaac7-c04d-45d9-a0e3-b81550aceec9',
--      '0186f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- GUEST ABOUT US
--     ('3b5510a9-e7c4-41b2-9574-9a7d483ea893', '4d8aaac7-c04d-45d9-a0e3-b81550aceec9',
--      '0286f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- GUEST SING UP
--     ('3616d546-943b-43d2-9798-dcfe430a9a82', '4d8aaac7-c04d-45d9-a0e3-b81550aceec9',
--      '0386f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- GUEST LOGIN
--     ('d589650e-bc9d-4896-8b40-ad350dc17839', '4d8aaac7-c04d-45d9-a0e3-b81550aceec9',
--      '0486f63b-c75b-441a-9867-17b0fbfd7d60'),
 -----------------------------------------
 -- BASIC YOUR_MRIYA_BOT
     ('7daa867b-c1d9-44be-8652-a1d759975b27', '09ba693b-2695-4ca7-bde6-02a41e9c3e79',
      '0586f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- BASIC MARIYA
     ('9e85b240-a090-4d00-85cd-69b56831e95f', '09ba693b-2695-4ca7-bde6-02a41e9c3e79',
      '0686f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- BASIC EVA
     ('6b96176f-b3ca-4a55-b0a8-44cd31e471ed', '09ba693b-2695-4ca7-bde6-02a41e9c3e79',
      '0786f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- BASIC WEB_STATS
     ('e844229b-94e4-4f27-9042-07911d422ab2', '09ba693b-2695-4ca7-bde6-02a41e9c3e79',
      '0886f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- BASIC ABOUT_US
--     ('1720492c-aacb-4d0f-97c9-49e7c81e9345', '09ba693b-2695-4ca7-bde6-02a41e9c3e79',
--      '0286f63b-c75b-441a-9867-17b0fbfd7d60'),
 ---------------------------------------
 -- MIDDLE MRIYA_BOT
     ('be3da632-2b13-4b38-8943-37c9170ab29a', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0586f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- MIDDLE MRIYA
     ('c034267e-36ab-45fe-98d9-8f871e89b4d8', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0686f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- MIDDLE EVA
     ('ccfc5325-679c-4886-9d49-4b1d89b3a06d', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0786f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- MIDDLE POLLS_BUILDER
     ('0baa3a1b-867b-4110-b983-7a98060a4a05', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0986f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- MIDDLE MESSAGE_BUILDER
     ('57918266-9465-480a-9b3e-c4ebce3d6c3b', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0a86f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- MIDDLE WEB_STATS
     ('1c920166-4305-484b-968e-d5b9dcd0ae3c', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0886f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- MIDDLE POLL_STATISTIC
     ('f8bea11b-267a-42cb-a781-ce3279660ed9', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
      '0b86f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- MIDDLE ABOUT_US
--     ('9bc2beef-6f31-4f34-81d4-70018ef15b31', '555a6f05-7ffb-49cc-bbda-6e85dda5ecb8',
--      '0286f63b-c75b-441a-9867-17b0fbfd7d60'),
 ----------------------------------------
 -- PREMIUM MRIYA_BOT
     ('8272ca44-bc7f-4e33-ac7a-5ef58e8f388a', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0586f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM MRIYA
     ('26375a60-90a0-4e09-9401-115386fe9412', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0686f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM EVA
     ('f6d1b129-4cd2-4530-b6ca-7b88ae5809e2', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0786f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM POLLS_BUILDER
     ('4690b66b-ba73-46bb-b8d3-c7728032f652', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0986f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM MESSAGE_BUILDER
     ('62c33a4b-ece1-41dd-8822-aa2a8ce9d3fb', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0a86f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM WEB_STATS
     ('4a4550b0-11d8-4c74-8500-448314f97f20', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0886f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM POLL_STATISTIC
     ('2d47d6ae-f8eb-4467-8da7-c7cbb9eaf39c', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '0b86f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- PREMIUM TARGET_AUDIENCE_PROFILE
     ('dc3194c8-52b3-4256-9d33-9bcd5d4c44ea', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
      '9b86f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- PREMIUM ABOUT_US
--     ('fa916afc-4094-403d-991a-325ee1ccb8d4', 'ba6e8456-8603-48e8-8bee-2df521eb8f0b',
--      '0286f63b-c75b-441a-9867-17b0fbfd7d60'),
 --------------------------------------
-- ADMIN MRIYA_BOT
     ('f3b5a71a-10f4-4d35-9552-30244213ca07', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0586f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN MRIYA
     ('8f3b28e1-7daf-418c-b8ba-f5974b3663f8', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0686f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN EVA
     ('aba241f4-a556-4110-84fc-252db9727c0d', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0786f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN POLLS_BUILDER
     ('b2b1fd92-9628-4160-a05b-7399737fd5ef', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0986f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN MESSAGE_BUILDER
     ('5b9fa7b3-2800-4bdd-9df5-29d9e433fc38', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0a86f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN WEB_STATS
     ('02d8d99d-58de-4977-94d2-e1cfe997f2e3', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0886f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN POLL_STATISTIC
     ('c52668e3-ee6b-423d-8181-7d5e349dd9e0', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0b86f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN TARGET_AUDIENCE_PROFILE
     ('90451310-a33c-45ec-9806-2bbf77c31690', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '9b86f63b-c75b-441a-9867-17b0fbfd7d60'),
-- -- ADMIN ABOUT_US
--     ('62137c7c-c2ce-42b6-acf7-37f286c30b4a', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
--      '0286f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN LOGS
     ('62137c7c-c2ce-42b6-acf7-37f787c30b4a', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0c86f63b-c75b-441a-9867-17b0fbfd7d60'),
 -- ADMIN CHANNELS
     ('62137c7c-c2ce-42b6-acf7-37f987c30b4a', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0d86f63b-c75b-441a-9867-17b0fbfd7d60'),
-- ADMIN SUBSCRIPTION
     ('62137c7c-c2ce-42b6-acf7-37f187c30b4a', 'ba6e8456-8603-48e8-8bee-2df531e48f0b',
      '0e86f63b-c75b-441a-9867-17b0fbfd7d60');