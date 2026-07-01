//```java id="vending_machine_lld"
/*
============================================================
VENDING MACHINE SYSTEM
============================================================


PROBLEM STATEMENT
============================================================


Design a Vending Machine system.

The machine should support:


- Display products
- Select product
- Insert money
- Validate payment
- Dispense item
- Return change
- Handle invalid operations



Example:


User selects:

Coke


Price:

50


User inserts:

100


Machine:


Dispense Coke


Return:

50 change




============================================================
CORE ENTITIES
============================================================



VendingMachine


        |
        |
        +----------------+
        |                |
     Inventory        State



Product


Contains:

- id
- name
- price
- quantity



============================================================
DESIGN DECISIONS
============================================================



1. STATE DESIGN PATTERN


Vending machine behavior changes based on state.



Example:



Idle State:


Waiting for user



Has Money State:


Accepting product selection



Dispense State:


Giving product



Instead of:


if money inserted

else if product selected


we create states.





VendingMachineState


        |

        |

-----------------------------

IdleState

HasMoneyState

DispenseState





============================================================


2. SINGLE RESPONSIBILITY


Product:

Stores product details



Inventory:

Manages products



Payment:

Handles money



State:

Controls machine behavior



VendingMachine:

Coordinates everything



============================================================


FLOW
============================================================


Machine starts


        |

        v


Idle State


        |

User inserts money


        |

        v


Has Money State


        |

Select product


        |

        v


Check inventory


        |

        v


Dispense product


        |

        v


Return change


        |

        v


Back to Idle



============================================================
TIME COMPLEXITY
============================================================


Select product:

HashMap lookup

O(1)



Insert money:

O(1)



Dispense:

O(1)



============================================================
SPACE COMPLEXITY
============================================================


O(products)



============================================================
INTERVIEW FOLLOW UPS
============================================================



1. Multiple payment methods


Add:


PaymentStrategy


        |

        |

CardPayment

CashPayment

UPIPayment



------------------------------------------------------------



2. Multiple machines


Add:


VendingMachineManager



------------------------------------------------------------



3. Inventory refill


Add:


SupplierService



------------------------------------------------------------



4. Concurrency


Two users selecting same product.


Use:


synchronized



============================================================

*/


import java.util.*;

public class VendingMachine {



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
    INVENTORY

    Stores products

    ========================================================
    */


    static class Inventory {


        Map<Integer, Product> products;



        Map<Integer,Integer> quantity;




        Inventory(){


            products =
                    new HashMap<>();


            quantity =
                    new HashMap<>();


        }






        public void addProduct(
                Product product,
                int count
        ){


            products.put(
                    product.id,
                    product
            );


            quantity.put(
                    product.id,
                    count
            );


        }






        public boolean available(
                int id
        ){


            return quantity.getOrDefault(
                    id,
                    0
            ) > 0;


        }






        public Product removeProduct(
                int id
        ){


            quantity.put(
                    id,
                    quantity.get(id)-1
            );


            return products.get(id);


        }


    }









    /*
    ========================================================
    STATE INTERFACE

    State pattern

    ========================================================
    */


    interface VendingMachineState {



        void insertMoney(
                VendingMachine machine,
                double money
        );



        void selectProduct(
                VendingMachine machine,
                int productId
        );



        void dispense(
                VendingMachine machine
        );



    }









    /*
    ========================================================
    IDLE STATE


    Waiting for money

    ========================================================
    */


    static class IdleState
            implements VendingMachineState {



        public void insertMoney(
                VendingMachine machine,
                double money
        ){


            machine.balance += money;


            machine.state =
                    new HasMoneyState();



            System.out.println(
                    "Money inserted"
            );


        }





        public void selectProduct(
                VendingMachine machine,
                int id
        ){


            System.out.println(
                    "Insert money first"
            );


        }




        public void dispense(
                VendingMachine machine
        ){



        }


    }









    /*
    ========================================================
    HAS MONEY STATE

    User has inserted money

    ========================================================
    */


    static class HasMoneyState
            implements VendingMachineState {



        public void insertMoney(
                VendingMachine machine,
                double money
        ){


            machine.balance += money;


        }






        public void selectProduct(
                VendingMachine machine,
                int productId
        ){



            Product product =
                    machine.inventory
                    .products
                    .get(productId);





            if(product == null){


                System.out.println(
                        "Product not found"
                );


                return;


            }






            if(
                !machine.inventory
                .available(productId)
            ){


                System.out.println(
                        "Out of stock"
                );


                return;


            }






            if(
                machine.balance
                <
                product.price
            ){


                System.out.println(
                        "Insufficient money"
                );


                return;


            }






            machine.selectedProduct =
                    productId;




            machine.state =
                    new DispenseState();



            machine.dispense();



        }






        public void dispense(
                VendingMachine machine
        ){

        }



    }









    /*
    ========================================================
    DISPENSE STATE

    ========================================================
    */


    static class DispenseState
            implements VendingMachineState {



        public void insertMoney(
                VendingMachine machine,
                double money
        ){



        }






        public void selectProduct(
                VendingMachine machine,
                int id
        ){



        }







        public void dispense(
                VendingMachine machine
        ){


            Product product =
                    machine.inventory
                    .removeProduct(
                            machine.selectedProduct
                    );




            machine.balance -=
                    product.price;




            System.out.println(
                    "Dispensed : "
                    +
                    product.name
            );




            System.out.println(
                    "Change returned : "
                    +
                    machine.balance
            );



            machine.balance = 0;



            machine.selectedProduct =
                    0;



            machine.state =
                    new IdleState();



        }


    }









    /*
    ========================================================
    VENDING MACHINE

    Main class

    ========================================================
    */


    Inventory inventory;



    VendingMachineState state;



    double balance;



    int selectedProduct;






    VendingMachine(){


        inventory =
                new Inventory();



        state =
                new IdleState();


    }







    public void insertMoney(
            double money
    ){

        state.insertMoney(
                this,
                money
        );

    }







    public void selectProduct(
            int id
    ){

        state.selectProduct(
                this,
                id
        );


    }








    public void dispense(){

        state.dispense(this);

    }









    /*
    ========================================================
    MAIN
    ========================================================
    */


    public static void main(String[] args) {



        VendingMachine machine =
                new VendingMachine();




        machine.inventory.addProduct(
                new Product(
                        1,
                        "Coke",
                        50
                ),
                5
        );



        machine.inventory.addProduct(
                new Product(
                        2,
                        "Chips",
                        30
                ),
                3
        );





        machine.insertMoney(
                100
        );




        machine.selectProduct(
                1
        );



    }


}
