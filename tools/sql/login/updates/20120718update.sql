INSERT INTO accounts_ipauth (`login`, `ip`, `type`) (SELECT login, userIP, 'allow' FROM accounts WHERE userIP IS NOT NULL);
ALTER TABLE accounts DROP userIP;