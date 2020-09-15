-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema finjavafx3
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema finjavafx3
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `finjavafx3` DEFAULT CHARACTER SET utf8 ;
USE `finjavafx3` ;

-- -----------------------------------------------------
-- Table `finjavafx3`.`despesa`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjavafx3`.`despesa` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL DEFAULT NULL,
  `preco` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 68
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjavafx3`.`tipopag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjavafx3`.`tipopag` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjavafx3`.`status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjavafx3`.`status` (
  `id` INT(11) NOT NULL,
  `nome` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjavafx3`.`lancamento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjavafx3`.`lancamento` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `referencia` VARCHAR(45) NULL DEFAULT NULL,
  `tipopag_id` INT(11) NULL DEFAULT NULL,
  `total` DECIMAL(10,2) NULL DEFAULT '0.00',
  `data` DATETIME NULL DEFAULT NULL,
  `status_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Lancament_tipopag_idx` (`tipopag_id` ASC),
  INDEX `fk_lancamento_status1_idx` (`status_id` ASC),
  CONSTRAINT `fk_Lancament_tipopag`
    FOREIGN KEY (`tipopag_id`)
    REFERENCES `finjavafx3`.`tipopag` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_lancamento_status1`
    FOREIGN KEY (`status_id`)
    REFERENCES `finjavafx3`.`status` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 87
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjavafx3`.`item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjavafx3`.`item` (
  `Lancamento_id` INT(11) NOT NULL DEFAULT '0',
  `despesa_id` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Lancamento_id`, `despesa_id`),
  INDEX `fk_Lancamento_has_despesa_despesa1_idx` (`despesa_id` ASC),
  INDEX `fk_Lancamento_has_despesa_Lancamento1_idx` (`Lancamento_id` ASC),
  CONSTRAINT `fk_Lancamento_has_despesa_Lancamento1`
    FOREIGN KEY (`Lancamento_id`)
    REFERENCES `finjavafx3`.`lancamento` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Lancamento_has_despesa_despesa1`
    FOREIGN KEY (`despesa_id`)
    REFERENCES `finjavafx3`.`despesa` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
