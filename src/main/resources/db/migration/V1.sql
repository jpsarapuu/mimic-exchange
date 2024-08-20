CREATE TABLE IF NOT EXISTS "user" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "transaction" (
    "id" SERIAL PRIMARY KEY,
    "user_id" INT NOT NULL,
    "type" VARCHAR NOT NULL,
    "amount" NUMERIC NOT NULL,
    CONSTRAINT "fk_user" FOREIGN KEY("user_id") REFERENCES "user"("id")
);

CREATE TABLE IF NOT EXISTS "exchange" (
    "id" SERIAL PRIMARY KEY,
    "user_id" INT NOT NULL,
    "type" VARCHAR NOT NULL,
    "asset_code" VARCHAR NOT NULL,
    "amount" NUMERIC NOT NULL,
    "unit_price" NUMERIC NOT NULL,
    "total_price" NUMERIC NOT NULL,
    CONSTRAINT "fk_user" FOREIGN KEY("user_id") REFERENCES "user"("id")
);

CREATE INDEX "transaction_user_id_idx" ON "transaction" ("user_id");
CREATE INDEX "exchange_user_id_idx" ON "exchange" ("user_id");