-- One-time update for an existing pet_health_tracker database.
-- Run it once in SSMS (New Query) BEFORE starting the backend from this version.
-- Hibernate (ddl-auto=update) adds new columns but never renames or drops old ones,
-- so these changes have to be made by hand.

USE pet_health_tracker;
GO

-- 1. Medication: dose_frequency was renamed to directions_for_use (keeps the existing values).
EXEC sp_rename 'medication.dose_frequency', 'directions_for_use', 'COLUMN';
GO

-- 2. Appointment: the stored status (UPCOMING / COMPLETED / CANCELLED) is replaced by a
--    cancelled flag. Upcoming / Past is now worked out from the date.
ALTER TABLE appointment ADD cancelled bit NOT NULL CONSTRAINT DF_appointment_cancelled DEFAULT 0;
GO

UPDATE appointment SET cancelled = 1 WHERE status = 'CANCELLED';

-- Hibernate created a CHECK constraint on status; it has to go before the column can be dropped.
DECLARE @check sysname = (
    SELECT name FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID('appointment') AND definition LIKE '%status%');
IF @check IS NOT NULL EXEC('ALTER TABLE appointment DROP CONSTRAINT ' + @check);

ALTER TABLE appointment DROP COLUMN status;
GO
