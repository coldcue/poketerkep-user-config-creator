# node-guerrilla
## API wrapper for http://www.guerrillamail.com



```CoffeeScript
gn = require 'node-guerrilla'

# Creates a new random email
gn.get_email()
.then (account) ->
  console.log account
  # account result
  # {
  #   msh: 'making new addre',
  #   email_addr: 'yfmhsjov@guerrillamailblock.com',
  #   email_timestamp: 1391814667,
  #   alias: 'e9jmpf2+osding',
  #   sid_token: 'jc1u7n2g08a34kklchiugg84j6'
  # }


  # Checks email on an interval and searches for an email that
  # has the subject 'Test Email' and a link in the body that matches 'string_to_find'

  gn.get_link_poll 'Test Email', 'string_to_find', account.sid_token
  .then (link) ->
    console.log link
```
