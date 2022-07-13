La aplicación es un servicio que calcula la facturación de un procesador de pagos basándose en las transacciones realizadas por cada ecommerce gestionado por él. También permitirá la creación de nuevos procesadores de pagos y nuevos ecommerce, así cómo modificar las tarifas que aplica cada procesador de pago en cualquier momento.

Para ello, se ha utilizado Spring como framework y JPA para los datos, estándo estos alojados en una base de datos MySQL. Tiene un front sencillo para poder realizar cada una de las operaciones disponibles en el servicio.

Se ha creado la base de datos "billing" con las tablas "ecommerce", "payment_processor" y "transactions" para alojar los datos de cada uno de los modelos. Estos son los script de creación:

#Se usa decimal(4,4) para las tarifas para que sólo permita decimales entre 0 y 1.
CREATE TABLE `payment_processor` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
 `flat_fee` decimal(4,4) NOT NULL,
 `app_lv` decimal(4,4) NOT NULL,
 `app_hv` decimal(4,4) NOT NULL,
 `fee_indicator` int(11) NOT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin

CREATE TABLE `ecommerce` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `name` varchar(255) COLLATE utf8_bin NOT NULL,
 `pp_id` int(11) NOT NULL,
 PRIMARY KEY (`id`),
 KEY `ecommerce_ibfk_1` (`pp_id`),
 CONSTRAINT `ecommerce_ibfk_1` FOREIGN KEY (`pp_id`) REFERENCES `payment_processor` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin

CREATE TABLE `transactions` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `ec_id` int(11) NOT NULL,
 `month` int(11) NOT NULL,
 `year` int(11) NOT NULL,
 `volume` int(11) NOT NULL,
 `amount` decimal(10,2) NOT NULL,
 PRIMARY KEY (`id`),
 KEY `ec_id` (`ec_id`),
 CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`ec_id`) REFERENCES `ecommerce` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin

En el caso de transactions, se han insertado unos valores para poder probar el servicio, se ha supuesto que en el caso real las transacciones se habrían creado a lo largo del mes.

El servicio se podrá usar tanto por web como por API. Los endpoint y variables se detallan a continuación:

/createPaymentProcessor POST
name (text)
flatFee (number between 0 and 1, ex: 0.01 -> 1%)
acquirerPlusLV (number between 0 and 1, ex: 0.01 -> 1%)
acquirerPlusHV (number between 0 and 1, ex: 0.01 -> 1%)
feeIndicator (Integer, transaction volume to decide the app to apply)

Returns a JSON with a result indicating the id generated
{
    "result": "Payment Processor successfuly created. Id -> 7."
}


/createEcommerce POST
name (text)
paymentProcessorId (Integer, id of the PP linked to)

Returns a JSON with a result indicating the id generated
{
    "result": "Ecommerce successfuly created. Id -> 8."
}


/billingInfo POST
paymentProcessorId (Integer, id of the PP you want to know the billing of)
month (Integer)
year (Integer)

Returns a JSON with "billList", a list containing all the ecommerce linked and the amount of the bill, and a "result", indicating if any error in the process.
{
    "billList": [
        {
            "ecommerce": "ECOMMERCE1",
            "amount": 378.06
        },
        {
            "ecommerce": "ECOMMERCE2",
            "amount": 256.00
        },
        {
            "ecommerce": "ECOMMERCE3",
            "amount": 480.00
        }
    ],
    "result": "ok"
}

/modifyFee
name (text)
flatFee (number between 0 and 1, ex: 0.01 -> 1%)
acquirerPlusLV (number between 0 and 1, ex: 0.01 -> 1%)
acquirerPlusHV (number between 0 and 1, ex: 0.01 -> 1%)
feeIndicator (Integer, transaction volume to decide the app to apply)

Returns a JSON with the result indicating how was the process.
{
    "result": "Changes applied successfully"
}


Via web, all the parameters will be required in a form for each action.
