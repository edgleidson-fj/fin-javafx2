-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema finjfx
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema finjfx
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `finjfx` DEFAULT CHARACTER SET utf8 ;
USE `finjfx` ;

-- -----------------------------------------------------
-- Table `finjfx`.`despesa`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjfx`.`despesa` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL DEFAULT NULL,
  `preco` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjfx`.`tipopag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjfx`.`tipopag` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjfx`.`status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjfx`.`status` (
  `id` INT(11) NOT NULL,
  `nome` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjfx`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjfx`.`usuario` (
  `usuarioId` INT(11) NOT NULL AUTO_INCREMENT,
  `usuarioNome` VARCHAR(45) NOT NULL,
  `usuarioSenha` VARCHAR(45) NULL DEFAULT NULL,
  `logado` VARCHAR(45) NULL DEFAULT 'N',
  PRIMARY KEY (`usuarioId`))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjfx`.`lancamento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjfx`.`lancamento` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `referencia` VARCHAR(45) NULL DEFAULT NULL,
  `tipopag_id` INT(11) NULL DEFAULT '0',
  `total` DECIMAL(10,2) NULL DEFAULT '0.00',
  `data` DATETIME NULL DEFAULT NULL,
  `status_id` INT(11) NULL DEFAULT '1',
  `desconto` DECIMAL(10,2) NULL DEFAULT NULL,
  `acrescimo` DECIMAL(10,2) NULL DEFAULT NULL,
  `finalizado` VARCHAR(45) NULL DEFAULT 'N',
  `usuario_Id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Lancament_tipopag_idx` (`tipopag_id` ASC),
  INDEX `fk_lancamento_status1_idx` (`status_id` ASC),
  INDEX `fk_lancamento_usuario1_idx` (`usuario_Id` ASC),
  CONSTRAINT `fk_Lancament_tipopag`
    FOREIGN KEY (`tipopag_id`)
    REFERENCES `finjfx`.`tipopag` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_lancamento_status1`
    FOREIGN KEY (`status_id`)
    REFERENCES `finjfx`.`status` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_lancamento_usuario1`
    FOREIGN KEY (`usuario_Id`)
    REFERENCES `finjfx`.`usuario` (`usuarioId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finjfx`.`item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finjfx`.`item` (
  `Lancamento_id` INT(11) NOT NULL DEFAULT '0',
  `despesa_id` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Lancamento_id`, `despesa_id`),
  INDEX `fk_Lancamento_has_despesa_despesa1_idx` (`despesa_id` ASC),
  INDEX `fk_Lancamento_has_despesa_Lancamento1_idx` (`Lancamento_id` ASC),
  CONSTRAINT `fk_Lancamento_has_despesa_Lancamento1`
    FOREIGN KEY (`Lancamento_id`)
    REFERENCES `finjfx`.`lancamento` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Lancamento_has_despesa_despesa1`
    FOREIGN KEY (`despesa_id`)
    REFERENCES `finjfx`.`despesa` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
