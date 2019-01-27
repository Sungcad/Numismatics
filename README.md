# Numismatics
A new view of your money.
## Description:
Numismatics provides a configurable way to view your money or the money of others, as well as provides a flexible way to send it. Say you want to send 10 billion dollars to XMen normally you would need to type out /pay XMen 10000000000 but with numismatics you can instead type /pay XMen 10B or /pay XMen $10,000,000,000

## Commands:
/balance (player) : see the balance of yourself or target player (rounded)
/ebalance (player) : see the balance of yourself or target player (exact)
/baltop (page) : see the top balances of the servers players (rounded)
/ebaltop (page) : see the top balances of the servers players (exact)
/eco <give|set|take> <player> <amount> : change a players balance
/numismatics (help|info|reload) : view information about the plugin or reload the config
/pay <player|*> <amount> : send another player an amount of money

## Permissions:
numismatics.balance.self : access to /balance
numismatics.balance.others: access to /balance (player)
numismatics.ebalance: access to /ebalance
numismatics.ebalance.others: access to /ebalance (player)
numismatics.baltop: access to /baltop
numismatics.ebaltop: access to /ebaltop
numismatics.pay: access to /pay
numismatics.pay.payall: access to /pay * <amount>
numismatics.economy: access to /eco
numismatics.info: access to /numismatics
numismatics.help: access to /numismatics help
numismatics.reload: access to /numismatics reload
numismatics.admin: access all commands

## Installation:
Requires Vault and an economy plugin. VanishNoPacket is not needed but will hide players from baltop and ebaltop unless you have the permission "vanish.see" or baltop.online is set to false in the config.
