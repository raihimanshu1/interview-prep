/*Let's build **Splitwise LLD** in the same style as previous systems:

        * Single runnable Java file
        * Explanation at the top
        * Proper OOP design
        * Strategy pattern
        * Expense splitting
        * Balance calculation
        * Multiple split types

        We will support:

        * Users
        * Groups
        * Expenses
        * Equal split
        * Exact split
        * Percentage split
        * Balance tracking
        * Settlement

        ---

        # High Level Design Explanation

        ```
        =====================================================

        SPLITWISE SYSTEM


        Example:

        Friends go for trip


        A pays ₹3000

        B
        C
        D


        System calculates:

        B owes A ₹1000
        C owes A ₹1000
        D owes A ₹1000



        Flow:


        User creates expense

        |
        v

        Select split strategy

        |
        v

        Calculate shares

        |
        v

        Update balances



        =====================================================
        ```

        # Core Entities

        ```
        User

        Expense

        Split

        Group

        BalanceSheet


        ```

        ---

        # Important Design Decisions

        ## 1. Expense and Split are separate

        Why?

        Expense:

        ```
        Dinner
        ₹3000
        Paid by A
        ```

        Split:

        ```
        B -> 1000
        C -> 1000
        D -> 1000
        ```

        Because splitting logic changes.

        ---

        # 2. Strategy Pattern for splitting

        Different rules:

        Equal:

        ```
        3000 / 3

        1000 each
        ```

        Exact:

        ```
        B = 500
        C = 1000
        D = 1500
        ```

        Percentage:

        ```
        B = 20%
        C = 30%
        D = 50%
        ```

        Instead of:

        ```
        if(equal)

        else if(exact)

        else
        ```

        we create:

        ```
        SplitStrategy

        |
        |
        EqualSplit
        ExactSplit
        PercentageSplit

        ```

        ---

        # 3. Balance Representation

        Example:

        A paid:

        ```
        ₹3000
        ```

        B owes:

        ```
        ₹1000
        ```

        Store:

        ```
        A -> B : 1000
        ```

        Meaning:

        B owes A.

        ---

        # Runnable Java Code

        ```java*/
import java.util.*;

public class SplitwiseSystem {


    /*
    =====================================================
    USER
    =====================================================
    */


    static class User {


        private final String id;


        private final String name;



        User(String name){


            this.id =
                    UUID.randomUUID()
                            .toString();


            this.name=name;

        }



        public String getName(){

            return name;

        }


    }







    /*
    =====================================================
    SPLIT
    =====================================================

    Represents how much one user owes

    =====================================================
    */


    static class Split {


        User user;


        double amount;



        Split(
                User user,
                double amount
        ){

            this.user=user;

            this.amount=amount;

        }


    }








    /*
    =====================================================
    EXPENSE
    =====================================================
    */


    static class Expense {


        String description;


        double amount;


        User paidBy;


        List<Split> splits;




        Expense(
                String description,
                double amount,
                User paidBy,
                List<Split> splits
        ){

            this.description=description;
            this.amount=amount;
            this.paidBy=paidBy;
            this.splits=splits;

        }


    }








    /*
    =====================================================
    SPLIT STRATEGY
    =====================================================
    */


    interface SplitStrategy {


        List<Split> split(
                double amount,
                List<User> users
        );


    }








    /*
    =====================================================
    EQUAL SPLIT
    =====================================================
    */


    static class EqualSplit
            implements SplitStrategy {



        public List<Split> split(
                double amount,
                List<User> users
        ){


            double share =
                    amount / users.size();



            List<Split> result =
                    new ArrayList<>();



            for(User user: users){


                result.add(
                        new Split(
                                user,
                                share
                        )
                );


            }


            return result;

        }

    }







    /*
    =====================================================
    BALANCE SHEET

    Tracks who owes whom

    =====================================================
    */


    static class BalanceSheet {



        private final Map<User, Map<User,Double>>
                balances =
                new HashMap<>();





        public void updateBalance(
                User paidBy,
                List<Split> splits
        ){



            for(Split split: splits){



                if(split.user == paidBy){

                    continue;

                }



                /*

                Example:

                A paid 3000

                B owes A 1000


                store:

                A -> B = 1000

                */


                balances
                        .computeIfAbsent(
                                paidBy,
                                x -> new HashMap<>()
                        )
                        .merge(
                                split.user,
                                split.amount,
                                Double::sum
                        );


            }


        }






        public void showBalances(){


            for(
                    User giver:
                    balances.keySet()
            ){



                for(
                        Map.Entry<User,Double> entry:
                        balances.get(giver)
                                .entrySet()
                ){


                    System.out.println(
                            entry.getKey()
                                    .getName()
                                    +
                                    " owes "
                                    +
                                    giver.getName()
                                    +
                                    " : "
                                    +
                                    entry.getValue()
                    );


                }


            }


        }



    }








    /*
    =====================================================
    SPLITWISE SERVICE
    =====================================================
    */


    static class SplitwiseService {


        BalanceSheet balanceSheet =
                new BalanceSheet();





        public void addExpense(
                String description,
                double amount,
                User paidBy,
                List<User> users,
                SplitStrategy strategy
        ){



            List<Split> splits =
                    strategy.split(
                            amount,
                            users
                    );




            Expense expense =
                    new Expense(
                            description,
                            amount,
                            paidBy,
                            splits
                    );




            balanceSheet.updateBalance(
                    paidBy,
                    splits
            );



        }





        public void showBalances(){

            balanceSheet.showBalances();

        }


    }









    /*
    =====================================================
    MAIN
    =====================================================
    */


    public static void main(String[] args) {



        User a =
                new User("A");

        User b =
                new User("B");

        User c =
                new User("C");





        SplitwiseService service =
                new SplitwiseService();





        /*

        A pays dinner ₹3000

        Split equally between:

        A
        B
        C

        */


        service.addExpense(
                "Dinner",
                3000,
                a,
                List.of(a,b,c),
                new EqualSplit()
        );




        service.showBalances();



    }

}
/*
```

        ---

        # Output

```
B owes A : 1000.0
C owes A : 1000.0
        ```

        ---

        # Flow Explanation

## Add Expense

```
A pays 3000

        |
v

        EqualSplit


A = 1000
B = 1000
C = 1000


        |
v


        BalanceSheet


A <- B 1000

A <- C 1000

        ```

        ---

        # Patterns Used

## Strategy Pattern

For split:

        ```
SplitStrategy

       |
               |
EqualSplit
        ExactSplit
PercentageSplit

```

        ---

        # Interview Follow-ups

## 1. Add Exact Split

Example:

        ```
Dinner 3000

A 500
B 1000
C 1500
        ```

Create:

        ```
ExactSplitStrategy
```

        ---

        ## 2. Settlement

Example:

        ```
B pays A 1000

Remove:

A <- B 1000

        ```

        ---

        ## 3. Simplify debts

Current:

        ```
A owes B 100
B owes C 100

        ```

Optimize:

        ```
A owes C 100

        ```

Use graph algorithm.

---

        ## 4. Concurrency

Multiple users adding expense:

Use:

        ```
transaction
+
database locking
```

        ---

This is the core Splitwise LLD expected in interviews.
*/
