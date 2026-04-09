
CREATE DATABASE IF NOT EXISTS Cricket_Scoreboard_OOP;

USE Cricket_Scoreboard_OOP;


-- ======================== Team ==========================
CREATE TABLE Team (
    TeamId       INT AUTO_INCREMENT PRIMARY KEY,
    Name         VARCHAR(100) NOT NULL,
    ShortName    VARCHAR(10),          -- e.g. PAK, IND
    CountryCode  VARCHAR(5),           -- e.g. pk, in (matches your flag codes)
    FlagPath     VARCHAR(255),         -- optional: icon path / URL
    CreatedAt    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================= Player =========================
CREATE TABLE Player (
    PlayerId      INT AUTO_INCREMENT PRIMARY KEY,
    TeamId        INT NOT NULL,
    FullName      VARCHAR(100) NOT NULL,
    Role          ENUM('BAT','BOWL','AR','WK') NOT NULL DEFAULT 'BAT',
    BattingStyle  VARCHAR(50),
    BowlingStyle  VARCHAR(50),
    DateOfBirth   DATE,
    IsActive      TINYINT(1) NOT NULL DEFAULT 1,
    CreatedAt     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_Player_Team
        FOREIGN KEY (TeamId) REFERENCES Team(TeamId)
        ON DELETE CASCADE ON UPDATE CASCADE
);



-- =============== Team Ranking ============================
CREATE TABLE IF NOT EXISTS TeamRanking (
    RankingId   INT AUTO_INCREMENT PRIMARY KEY,
    Position    INT NOT NULL,
    TeamName    VARCHAR(100) NOT NULL,
    Matches     INT NOT NULL,
    Points      INT NOT NULL,
    Rating      INT NOT NULL,
    CountryCode VARCHAR(10) NOT NULL,
    CONSTRAINT uq_teamranking_team UNIQUE (TeamName)
);

-- =============== Admin Users =============================
CREATE TABLE IF NOT EXISTS admin (
    AdminId INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50),
    Password VARCHAR(50)
);

-- =============== Simple Schedule =========================
CREATE TABLE IF NOT EXISTS Schedule (
    ScheduleId INT AUTO_INCREMENT PRIMARY KEY,
    TeamA VARCHAR(100) NOT NULL,
    TeamACode VARCHAR(10) NOT NULL,
    TeamB VARCHAR(100) NOT NULL,
    TeamBCode VARCHAR(10) NOT NULL,
    MatchDate DATE NOT NULL,
    MatchTime VARCHAR(20) NOT NULL,
    Venue VARCHAR(120) NOT NULL,
    Format VARCHAR(10) NOT NULL
);


------------------------------------------------------------
-- 6) STATIC DATA SEEDING
------------------------------------------------------------

-- =============== TEAMS ===================================
INSERT INTO Team (Name, ShortName, CountryCode, FlagPath) VALUES
('India',         'IND', 'in', 'in.png'),
('Pakistan',      'PAK', 'pk', 'pk.png'),
('Australia',     'AUS', 'au', 'au.png'),
('England',       'ENG', 'gb', 'gb.png'),
('South Africa',  'SA',  'za', 'za.png'),
('New Zealand',   'NZ',  'nz', 'nz.png'),
('Sri Lanka',     'SL',  'lk', 'lk.png'),
('Bangladesh',    'BAN', 'bd', 'bd.png'),
('West Indies',   'WI',  'jm', 'jm.png'),
('Zimbabwe',      'ZIM', 'zw', 'zw.png'),
('Afghanistan',   'AFG', 'af', 'af.png'),
('Ireland',       'IRE', 'ie', 'ie.png');

-- =============== MAIN SQUADS (BASE PLAYERS) ==============
INSERT INTO Player (TeamId, FullName, Role) VALUES
-- INDIA
((SELECT TeamId FROM Team WHERE Name='India'), 'Rohit Sharma', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='India'), 'Virat Kohli', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='India'), 'Shubman Gill', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='India'), 'Jasprit Bumrah', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='India'), 'Kuldeep Yadav', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='India'), 'Hardik Pandya', 'AR'),
((SELECT TeamId FROM Team WHERE Name='India'), 'Ravindra Jadeja', 'AR'),

-- PAKISTAN
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Babar Azam', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Mohammad Rizwan', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Abdullah Shafique', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Shaheen Afridi', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Haris Rauf', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Shadab Khan', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Pakistan'), 'Agha Salman', 'AR'),

-- AUSTRALIA
((SELECT TeamId FROM Team WHERE Name='Australia'), 'David Warner', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Australia'), 'Steve Smith', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Australia'), 'Marnus Labuschagne', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Australia'), 'Mitchell Starc', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Australia'), 'Pat Cummins', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Australia'), 'Glenn Maxwell', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Australia'), 'Marcus Stoinis', 'AR'),

-- ENGLAND
((SELECT TeamId FROM Team WHERE Name='England'), 'Jos Buttler', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='England'), 'Jonny Bairstow', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='England'), 'Joe Root', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='England'), 'Jofra Archer', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='England'), 'Mark Wood', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='England'), 'Ben Stokes', 'AR'),
((SELECT TeamId FROM Team WHERE Name='England'), 'Moeen Ali', 'AR'),

-- NEW ZEALAND
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Kane Williamson', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Devon Conway', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Finn Allen', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Trent Boult', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Tim Southee', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Daryl Mitchell', 'AR'),
((SELECT TeamId FROM Team WHERE Name='New Zealand'), 'Mitchell Santner', 'AR'),

-- SOUTH AFRICA
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Aiden Markram', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Reeza Hendricks', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Rassie van der Dussen', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Kagiso Rabada', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Anrich Nortje', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Marco Jansen', 'AR'),
((SELECT TeamId FROM Team WHERE Name='South Africa'), 'Aiden Markram', 'AR'),

-- BANGLADESH
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Litton Das', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Najmul Hossain Shanto', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Shakib Al Hasan', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Taskin Ahmed', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Mustafizur Rahman', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Mahmudullah', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Bangladesh'), 'Mehidy Hasan', 'AR'),

-- SRI LANKA
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Kusal Mendis', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Pathum Nissanka', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Charith Asalanka', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Maheesh Theekshana', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Dilshan Madushanka', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Wanindu Hasaranga', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Sri Lanka'), 'Angelo Mathews', 'AR'),

-- WEST INDIES
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Nicholas Pooran', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Shai Hope', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Brandon King', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Alzarri Joseph', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Akeal Hosein', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Jason Holder', 'AR'),
((SELECT TeamId FROM Team WHERE Name='West Indies'), 'Rovman Powell', 'AR'),

-- ZIMBABWE
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Craig Ervine', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Sean Williams', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Wesley Madhevere', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Richard Ngarava', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Blessing Muzarabani', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Sikandar Raza', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Zimbabwe'), 'Ryan Burl', 'AR'),

-- AFGHANISTAN
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Rahmanullah Gurbaz', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Ibrahim Zadran', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Hashmatullah Shahidi', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Rashid Khan', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Naveen-ul-Haq', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Mohammad Nabi', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Afghanistan'), 'Azmatullah Omarzai', 'AR'),

-- IRELAND
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'Andrew Balbirnie', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'Paul Stirling', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'Harry Tector', 'BAT'),
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'Mark Adair', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'Josh Little', 'BOWL'),
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'Curtis Campher', 'AR'),
((SELECT TeamId FROM Team WHERE Name='Ireland'), 'George Dockrell', 'AR');

-- =============== EXTRA PLAYERS (ALL TEAMS, CLUBBED) ======
INSERT INTO Player (TeamId, FullName, Role)
SELECT t.TeamId, p.FullName, p.Role
FROM Team t
JOIN (
    -- INDIA
    SELECT 'India' AS TeamName, 'Shreyas Iyer' AS FullName, 'BAT' AS Role UNION ALL
    SELECT 'India', 'Suryakumar Yadav', 'BAT' UNION ALL
    SELECT 'India', 'Mohammed Shami', 'BOWL' UNION ALL
    SELECT 'India', 'Rishabh Pant', 'AR' UNION ALL

    -- PAKISTAN
    SELECT 'Pakistan', 'Fakhar Zaman', 'BAT' UNION ALL
    SELECT 'Pakistan', 'Imam-ul-Haq', 'BAT' UNION ALL
    SELECT 'Pakistan', 'Mohammad Nawaz', 'AR' UNION ALL
    SELECT 'Pakistan', 'Naseem Shah', 'BOWL' UNION ALL

    -- AUSTRALIA
    SELECT 'Australia', 'Travis Head', 'BAT' UNION ALL
    SELECT 'Australia', 'Mitchell Marsh', 'AR' UNION ALL
    SELECT 'Australia', 'Josh Hazlewood', 'BOWL' UNION ALL
    SELECT 'Australia', 'Adam Zampa', 'BOWL' UNION ALL

    -- ENGLAND
    SELECT 'England', 'Dawid Malan', 'BAT' UNION ALL
    SELECT 'England', 'Harry Brook', 'BAT' UNION ALL
    SELECT 'England', 'Chris Woakes', 'AR' UNION ALL
    SELECT 'England', 'Adil Rashid', 'BOWL' UNION ALL

    -- NEW ZEALAND
    SELECT 'New Zealand', 'Glenn Phillips', 'AR' UNION ALL
    SELECT 'New Zealand', 'Tom Latham', 'AR' UNION ALL
    SELECT 'New Zealand', 'Jimmy Neesham', 'AR' UNION ALL
    SELECT 'New Zealand', 'Lockie Ferguson', 'BOWL' UNION ALL

    -- SOUTH AFRICA
    SELECT 'South Africa', 'Quinton de Kock', 'AR' UNION ALL
    SELECT 'South Africa', 'Heinrich Klaasen', 'AR' UNION ALL
    SELECT 'South Africa', 'David Miller', 'BAT' UNION ALL
    SELECT 'South Africa', 'Tabraiz Shamsi', 'BOWL' UNION ALL

    -- BANGLADESH
    SELECT 'Bangladesh', 'Mushfiqur Rahim', 'AR' UNION ALL
    SELECT 'Bangladesh', 'Towhid Hridoy', 'BAT' UNION ALL
    SELECT 'Bangladesh', 'Shoriful Islam', 'BOWL' UNION ALL
    SELECT 'Bangladesh', 'Hasan Mahmud', 'BOWL' UNION ALL

    -- SRI LANKA
    SELECT 'Sri Lanka', 'Sadeera Samarawickrama', 'BAT' UNION ALL
    SELECT 'Sri Lanka', 'Dhananjaya de Silva', 'AR' UNION ALL
    SELECT 'Sri Lanka', 'Kasun Rajitha', 'BOWL' UNION ALL
    SELECT 'Sri Lanka', 'Lahiru Kumara', 'BOWL' UNION ALL

    -- WEST INDIES
    SELECT 'West Indies', 'Shimron Hetmyer', 'BAT' UNION ALL
    SELECT 'West Indies', 'Kyle Mayers', 'AR' UNION ALL
    SELECT 'West Indies', 'Oshane Thomas', 'BOWL' UNION ALL
    SELECT 'West Indies', 'Romario Shepherd', 'AR' UNION ALL

    -- ZIMBABWE
    SELECT 'Zimbabwe', 'Innocent Kaia', 'BAT' UNION ALL
    SELECT 'Zimbabwe', 'Brad Evans', 'BOWL' UNION ALL
    SELECT 'Zimbabwe', 'Tendai Chatara', 'BOWL' UNION ALL
    SELECT 'Zimbabwe', 'Milton Shumba', 'AR' UNION ALL

    -- AFGHANISTAN
    SELECT 'Afghanistan', 'Rahmat Shah', 'BAT' UNION ALL
    SELECT 'Afghanistan', 'Fazalhaq Farooqi', 'BOWL' UNION ALL
    SELECT 'Afghanistan', 'Gulbadin Naib', 'AR' UNION ALL
    SELECT 'Afghanistan', 'Mujeeb Ur Rahman', 'BOWL' UNION ALL

    -- IRELAND
    SELECT 'Ireland', 'Lorcan Tucker', 'AR' UNION ALL
    SELECT 'Ireland', 'Graham Hume', 'BOWL' UNION ALL
    SELECT 'Ireland', 'Craig Young', 'BOWL' UNION ALL
    SELECT 'Ireland', 'Simi Singh', 'AR'
) AS p
ON p.TeamName = t.Name
WHERE NOT EXISTS (
    SELECT 1 FROM Player pl
    WHERE pl.TeamId = t.TeamId
      AND pl.FullName = p.FullName
);

-- =============== TEAM RANKINGS DATA ======================
INSERT INTO TeamRanking (Position, TeamName, Matches, Points, Rating, CountryCode) VALUES
(1,  'India',        39, 4745, 122, 'in'),
(2,  'New Zealand',  44, 4956, 113, 'nz'),
(3,  'Australia',    38, 4134, 109, 'au'),
(4,  'Pakistan',     41, 4294, 105, 'pk'),
(5,  'Sri Lanka',    44, 4392, 100, 'lk'),
(6,  'South Africa', 38, 3708,  98, 'za'),
(7,  'Afghanistan',  28, 2657,  95, 'af'),
(8,  'England',      40, 3432,  86, 'gb'),
(9,  'West Indies',  41, 3173,  77, 'jm'),
(10, 'Bangladesh',   38, 2882,  76, 'bd'),
(11, 'Zimbabwe',     24, 1291,  54, 'zw'),
(12, 'Ireland',      18,  938,  52, 'ie'),
(13, 'Scotland',     33, 1522,  46, 'gb-sct');

-- =============== ADMIN USER ==============================
INSERT INTO admin (Username, Password)
VALUES ('admin', '12345');

-- =============== SAMPLE SCHEDULE =========================
INSERT INTO Schedule (TeamA, TeamACode, TeamB, TeamBCode, MatchDate, MatchTime, Venue, Format) VALUES
('India',        'in', 'Pakistan',      'pk', '2025-02-21', '7:00 PM',  'Melbourne Cricket Ground', 'T20'),
('Australia',    'au', 'England',       'gb', '2025-02-28', '2:00 PM',  'Sydney Cricket Ground',    'ODI'),
('South Africa', 'za', 'New Zealand',   'nz', '2025-03-14', '1:30 PM',  'Johannesburg',             'T20'),
('Sri Lanka',    'lk', 'Bangladesh',    'bd', '2025-03-20', '6:30 PM',  'Colombo',                  'ODI'),
('West Indies',  'jm', 'Zimbabwe',      'zw', '2025-04-05', '4:00 PM',  'Bridgetown',               'T20'),
('Afghanistan',  'af', 'Ireland',       'ie', '2025-04-12', '3:00 PM',  'Sharjah',                  'T20');




CREATE TABLE IF NOT EXISTS matches (
    MatchId     INT AUTO_INCREMENT PRIMARY KEY,
    TeamAName   VARCHAR(100) NOT NULL,
    TeamBName   VARCHAR(100) NOT NULL,
    ScoreA      VARCHAR(50)  NOT NULL,   -- e.g. "150/7 (20.0)"
    ScoreB      VARCHAR(50)  NOT NULL,   -- e.g. "148/9 (20.0)"
    ResultText  VARCHAR(255) NOT NULL,   -- e.g. "India won by 2 runs"
    MatchDate   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =========================
--  BATSMAN SCORECARD
-- =========================
CREATE TABLE IF NOT EXISTS match_batsman (
    Id          INT AUTO_INCREMENT PRIMARY KEY,
    MatchId     INT NOT NULL,
    InningsNo INT NOT NULL,
    TeamName    VARCHAR(100) NOT NULL,
    PlayerName  VARCHAR(100) NOT NULL,
    Runs        INT NOT NULL,
    Balls       INT NOT NULL,
    Fours       INT NOT NULL,
    Sixes       INT NOT NULL,
    Dismissal   VARCHAR(255),

    CONSTRAINT fk_mb_match
        FOREIGN KEY (MatchId) REFERENCES matches(MatchId)
        ON DELETE CASCADE
);

-- =========================
--  BOWLER FIGURES
-- =========================

CREATE TABLE match_bowler (
    BowlerId INT AUTO_INCREMENT PRIMARY KEY,
    MatchId INT,
    InningsNo INT NOT NULL,
    TeamName VARCHAR(50),
    PlayerName VARCHAR(50),
    Overs DOUBLE,
    Runs INT,
    Wickets INT,
    Economy DOUBLE,
    FOREIGN KEY (MatchId) REFERENCES matches(MatchId)
        ON DELETE CASCADE
);



ALTER TABLE TeamRanking
ADD COLUMN Wins INT NOT NULL;


ALTER TABLE TeamRanking
ADD COLUMN Losses INT NOT NULL;


ALTER TABLE TeamRanking
ADD COLUMN Ties INT NOT NULL;



	