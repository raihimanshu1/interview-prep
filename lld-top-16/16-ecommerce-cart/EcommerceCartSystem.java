/*
============================================================
E-COMMERCE CART SYSTEM
============================================================


PROBLEM STATEMENT
============================================================


Design an e-commerce shopping cart system like:


Amazon
Flipkart



System should support:


- Add product to cart
- Remove product
- Update quantity
- Calculate cart total
- Apply discounts
- Checkout
- Create order
- Payment



Example:


User:


John



Adds:


Laptop

Quantity: 2



Cart:


Laptop x2


Checkout:


Calculate price


Apply discount


Make payment


Create order



============================================================
CORE ENTITIES
============================================================



EcommerceSystem


        |
        |
        +----------------+
        |                |
      User             Cart


                         |
                         |
                     CartItem


                         |
                         |
                     Product



Checkout:



Cart

 |

Order

 |

Payment



============================================================
DESIGN DECISIONS
============================================================



1. PRODUCT AND CART ITEM SEPARATION


Why CartItem?


Because same product can have different quantity.



Example:


Product:


iPhone



CartItem:


iPhone x3



Product stores:

name

price



CartItem stores:

quantity



------------------------------------------------------------



2. STRATEGY PATTERN FOR DISCOUNT


Discount changes frequently.



Example:


Normal discount:

10%



Festival:

50%



Premium user:

20%



Instead of:



if festival


else premium



Use:



DiscountStrategy


        |

        |

----------------

NoDiscount

PercentageDiscount



============================================================


3. PAYMENT STRATEGY


Different payment methods:



UPI

CARD

WALLET



Use:


PaymentStrategy



============================================================
FLOW
============================================================


User login


        |

        v


Browse products


        |

        v


Add to cart


        |

        v


Update quantity


        |

        v


Checkout


        |

        v


Apply discount


        |

        v


Payment


        |

        v


Create order




============================================================
TIME COMPLEXITY
============================================================



Add product:


HashMap lookup

O(1)



Remove product:


O(1)



Calculate total:


O(n)


n = cart items



Checkout:


O(n)



============================================================
SPACE COMPLEXITY
============================================================


O(products in cart)



============================================================
INTERVIEW FOLLOW UPS
============================================================



1. Inventory management


Before checkout:


Check stock



Add:


InventoryService



------------------------------------------------------------



2. Cart persistence


Store cart:


Database



Redis for temporary cart



------------------------------------------------------------



3. Order tracking


Add:



OrderStatus


CREATED

PAID

SHIPPED

DELIVERED



------------------------------------------------------------



4. Concurrent checkout


Two users buy last item.



Solution:


Database transaction


Lock inventory row



============================================================

*/


import java.util.*;

public class EcommerceCartSystem {



    /*
    ========================================================
    PRODUCT
    ========================================================
    */


    static class Product {


        int id;


        String name;


        double price;




        Product(
                int id,
                String name,
                double price
        ){

            this.id=id;

            this.name=name;

            this.price=price;

        }



    }









    /*
    ========================================================
    CART ITEM


    Represents product + quantity


    ========================================================
    */


    static class CartItem {


        Product product;


        int quantity;





        CartItem(
                Product product,
                int quantity
        ){

            this.product=product;

            this.quantity=quantity;

        }






        double getTotal(){


            return product.price * quantity;


        }



    }









    /*
    ========================================================
    CART


    Stores user shopping items


    ========================================================
    */


    static class Cart {


        Map<Integer, CartItem> items;



        Cart(){


            items =
                    new HashMap<>();


        }







        void addProduct(
                Product product
        ){



            if(
                items.containsKey(
                        product.id
                )
            ){


                items.get(
                        product.id
                ).quantity++;



            }
            else{


                items.put(
                        product.id,
                        new CartItem(
                                product,
                                1
                        )
                );


            }



        }








        void removeProduct(
                int productId
        ){


            items.remove(
                    productId
            );


        }








        double calculateTotal(){


            double total = 0;



            for(
                    CartItem item:
                    items.values()
            ){


                total +=
                        item.getTotal();


            }


            return total;


        }



    }









    /*
    ========================================================
    USER

    ========================================================
    */


    static class User {


        int id;


        String name;


        Cart cart;



        User(
                int id,
                String name
        ){

            this.id=id;

            this.name=name;


            cart =
                    new Cart();

        }



    }









    /*
    ========================================================
    DISCOUNT STRATEGY

    ========================================================
    */


    interface DiscountStrategy {


        double apply(
                double amount
        );


    }










    static class NoDiscount
            implements DiscountStrategy {



        public double apply(
                double amount
        ){


            return amount;


        }


    }








    static class PercentageDiscount
            implements DiscountStrategy {



        double percentage;



        PercentageDiscount(
                double percentage
        ){

            this.percentage =
                    percentage;

        }





        public double apply(
                double amount
        ){


            return amount
                    -
                    (
                    amount *
                    percentage /
                    100
                    );


        }



    }









    /*
    ========================================================
    PAYMENT STRATEGY
    ========================================================
    */


    interface PaymentStrategy {


        boolean pay(
                double amount
        );


    }










    static class CardPayment
            implements PaymentStrategy {



        public boolean pay(
                double amount
        ){


            System.out.println(
                    "Paid using Card : "
                    +
                    amount
            );


            return true;


        }


    }









    /*
    ========================================================
    ORDER
    ========================================================
    */


    static class Order {


        int id;


        double amount;



        Order(
                int id,
                double amount
        ){

            this.id=id;

            this.amount=amount;


        }


    }









    /*
    ========================================================
    CHECKOUT SERVICE

    ========================================================
    */


    static class CheckoutService {


        DiscountStrategy discountStrategy;



        PaymentStrategy paymentStrategy;







        CheckoutService(){


            discountStrategy =
                    new NoDiscount();


            paymentStrategy =
                    new CardPayment();


        }







        Order checkout(
                User user
        ){



            double total =
                    user.cart.calculateTotal();





            total =
                    discountStrategy.apply(
                            total
                    );






            boolean success =
                    paymentStrategy.pay(
                            total
                    );





            if(success){


                return new Order(
                        1,
                        total
                );


            }





            throw new RuntimeException(
                    "Payment failed"
            );


        }


    }









    /*
    ========================================================
    MAIN
    ========================================================
    */


    public static void main(String[] args) {



        Product laptop =
                new Product(
                        1,
                        "Laptop",
                        50000
                );



        Product mouse =
                new Product(
                        2,
                        "Mouse",
                        1000
                );





        User user =
                new User(
                        1,
                        "John"
                );





        user.cart.addProduct(
                laptop
        );


        user.cart.addProduct(
                laptop
        );


        user.cart.addProduct(
                mouse
        );





        CheckoutService checkout =
                new CheckoutService();





        checkout.discountStrategy =
                new PercentageDiscount(
                        10
                );





        Order order =
                checkout.checkout(
                        user
                );






        System.out.println(
                "Order Created : "
                +
                order.amount
        );



    }


}
