# Simple Stock managing app
first project

////////////////////////////////////////////////
This is my first side android app project using android studio.

//Main
You will see category, name, current stock left, item price and daily total on the item list.
By clicking item, it will move to an intent where you can modify the item's information.
By clicking modify button it will open an intent where we can adjust(when stock is changed) stock left or create new item.
By clicking 'Daily total' textbox, it shows a pop-up message that will reset the daily total.

//Modify
You will see the items created with checkbox, current stock left, and plus/minus button.
By clicking those buttons, you can adjust the stock.
The changing number can't exceed the current stock left nor goes under 0.
The checkbox is for promotion, it will be applied only when item is on promotion, and set checked automatically if it is under effect.

//Create New
You can create a new item.
By clicking promotion checkbox, a pop-up message will be shown for entering specific requirements
(e.g buy 3 make item $10)
If you check apply for promo, then all of the items under the same category will be on the specified promotion.

//Edit Item
You can edit the selected item.
You can edit category, name, price, stock, promotion availablity, and delete item.

