# React / Redux / API - Beginner To Interview Deep Dive

This file assumes:

```text
You are a beginner in React.
You need to explain clearly in interview.
You need simple examples first, then senior-level answer.
```

---

# 1. What Is React?

React is a JavaScript library for building user interfaces.

In simple terms:

```text
React helps build screens using small reusable pieces called components.
```

---

## Example

```jsx
function Welcome() {
  return <h1>Hello Himanshu</h1>;
}
```

This function is a:

```text
React component
```

---

## Why React Is Used

React helps with:

```text
Reusable UI components
State-based rendering
Fast UI updates
Large frontend applications
Clear separation of UI logic
```

---

## Mental Model

Think:

```text
UI = function of state
```

Meaning:

```text
When data changes, React updates the screen.
```

---

## Interview Answer

> React is a JavaScript library used to build user interfaces using reusable components. React follows the idea that UI is a function of state. When state changes, React re-renders the relevant UI. It is commonly used for single-page applications and frontend systems that need dynamic, interactive screens.

---

# 2. Component

## What Is A Component?

A component is a reusable piece of UI.

Example:

```text
Button
Header
AccountCard
TransactionList
LoginForm
```

---

## Example

```jsx
function AccountCard() {
  return (
    <div>
      <h2>Savings Account</h2>
      <p>Balance: 1000</p>
    </div>
  );
}
```

---

## Why Components Matter

Without components:

```text
Large UI becomes messy.
Code is repeated.
Testing becomes harder.
```

With components:

```text
UI is reusable.
Code is easier to understand.
Each component has one responsibility.
```

---

## Interview Answer

> A React component is a reusable UI building block. Components help break a large screen into smaller pieces like forms, buttons, cards, and lists. This improves readability, reuse, testing, and maintainability.

---

# 3. Props

## What Are Props?

Props are inputs passed from parent component to child component.

In simple terms:

```text
Props are like function arguments for components.
```

---

## Example

```jsx
function Greeting(props) {
  return <h1>Hello {props.name}</h1>;
}

function App() {
  return <Greeting name="Himanshu" />;
}
```

Output:

```text
Hello Himanshu
```

---

## Important Point

Props should be treated as:

```text
read-only
```

Child should not modify props directly.

---

## Interview Answer

> Props are data passed from a parent component to a child component. They make components reusable because the same component can render different data based on props. Props should be treated as read-only.

---

# 4. State

## What Is State?

State is data that belongs to a component and can change over time.

Examples:

```text
input value
loading flag
selected tab
API response data
error message
```

---

## Example

```jsx
import { useState } from "react";

function Counter() {
  const [count, setCount] = useState(0);

  return (
    <button onClick={() => setCount(count + 1)}>
      Count: {count}
    </button>
  );
}
```

When button clicked:

```text
state changes
React re-renders UI
new count appears
```

---

## Props vs State

| Concept | Meaning |
|---|---|
| Props | Data received from parent |
| State | Data managed inside component |
| Props mutable? | No |
| State mutable? | Updated using setter |

---

## Interview Answer

> State is data that changes over time inside a component. When state changes, React re-renders the component. Props are passed from parent to child, while state is owned by the component itself.

---

# 5. React + API + JSON

## What Is API?

API is a way for frontend and backend to talk.

Example:

```text
React frontend asks backend:
Give me account details.
```

Backend responds:

```json
{
  "accountId": 101,
  "balance": 5000
}
```

This format is:

```text
JSON
```

---

## Flow

```text
React screen loads
   |
calls backend API
   |
backend returns JSON
   |
React stores data in state
   |
React displays data
```

---

## Beginner Example

```jsx
import { useEffect, useState } from "react";

function Accounts() {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadAccounts() {
      try {
        const response = await fetch("/api/accounts");

        if (!response.ok) {
          throw new Error("Failed to load accounts");
        }

        const data = await response.json();
        setAccounts(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    loadAccounts();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <ul>
      {accounts.map(account => (
        <li key={account.accountId}>{account.balance}</li>
      ))}
    </ul>
  );
}
```

---

## Why `response.ok`?

`fetch()` does not fail automatically for HTTP 400 or 500.

So check:

```js
if (!response.ok) {
  throw new Error("Request failed");
}
```

---

## Production Things To Handle

```text
Loading state
Error state
Empty response
401 unauthorized
403 forbidden
500 server error
Timeout/cancellation
Invalid JSON
Backward-compatible API changes
```

---

## Interview Answer

> React calls backend APIs using tools like `fetch` or Axios. The backend usually returns JSON. React stores that data in state and renders UI from it. In production, I handle loading, error, empty state, HTTP status codes, authentication failures, and response compatibility.

---

# 6. What Is Redux?

Redux is a predictable state management library.

In simple terms:

```text
Redux is a central store for application state.
```

---

## Why Redux Was Needed

For small apps:

```text
useState is enough.
```

For large apps:

```text
Many components need same data.
Passing props becomes messy.
State updates become hard to track.
```

This is called:

```text
prop drilling
```

Redux solves this using a central store.

---

## Redux Building Blocks

```text
Store    -> holds application state
Action   -> describes what happened
Reducer  -> calculates next state
Dispatch -> sends action to reducer
Selector -> reads data from store
```

---

## Simple Flow

```text
User clicks deposit
   |
dispatch({ type: "DEPOSIT", amount: 100 })
   |
reducer calculates new balance
   |
store updates
   |
React re-renders
```

---

## Interview Answer

> Redux manages shared application state in a central store. Components dispatch actions, reducers calculate the next state, and components read state using selectors. I use Redux when state is shared across many components or updates need to be predictable and traceable.

---

# 7. Action

## What Is An Action?

An action is a plain JavaScript object that describes what happened.

Example:

```js
{
  type: "DEPOSIT",
  amount: 100
}
```

It does not update state by itself.

It only says:

```text
Something happened.
```

---

## Interview Answer

> An action is a plain object that describes an event in the application. It usually has a `type` field and may contain extra data called payload. Actions are dispatched to reducers.

---

# 8. Reducer

## What Is A Reducer?

A reducer is a function that takes:

```text
current state + action
```

and returns:

```text
new state
```

---

## Example

```js
const initialState = {
  balance: 0
};

function accountReducer(state = initialState, action) {
  switch (action.type) {
    case "DEPOSIT":
      return {
        ...state,
        balance: state.balance + action.amount
      };

    default:
      return state;
  }
}
```

---

## Reducers Must Be Pure

A pure reducer:

```text
does not mutate state
does not call API
does not use random values
does not change external variables
returns same output for same input
```

---

## Bad Reducer

```js
function reducer(state, action) {
  state.items.push(action.item);
  fetch("/api/items");
  return state;
}
```

Problems:

```text
mutates state
calls API
not predictable
```

---

## Good Reducer

```js
function reducer(state, action) {
  return {
    ...state,
    items: [...state.items, action.item]
  };
}
```

---

## Interview Answer

> A reducer calculates the next state from the current state and action. Reducers must be pure, so they should not mutate state or perform side effects like API calls. This makes Redux predictable, testable, and easier to debug.

---

# 9. Immutability In Redux

## What Is Immutability?

Immutability means:

```text
Do not change existing object.
Create a new copy with updated value.
```

---

## Bad

```js
state.balance = state.balance + 100;
return state;
```

This mutates existing state.

---

## Good

```js
return {
  ...state,
  balance: state.balance + 100
};
```

This creates new state object.

---

## Why It Matters

Redux detects changes using object references.

If same object is returned:

```text
React may not re-render correctly.
Debugging becomes harder.
Time-travel debugging breaks.
```

---

## Redux Toolkit Note

In Redux Toolkit, this looks allowed:

```js
state.balance += 100;
```

Why?

Because Redux Toolkit uses:

```text
Immer
```

Immer writes immutable updates behind the scenes.

---

## Interview Answer

> Immutability in Redux means we do not directly modify existing state. Instead, we return a new state object. This makes state changes predictable and helps React detect updates efficiently. Redux Toolkit uses Immer, so code can look mutable while still producing immutable updates internally.

---

# 10. `useSelector`

## What Is `useSelector`?

`useSelector` reads data from Redux store.

Example:

```js
const balance = useSelector(state => state.account.balance);
```

---

## Safe

Select only what you need:

```js
const balance = useSelector(state => state.account.balance);
```

---

## Unsafe

```js
const fullState = useSelector(state => state);
```

Problem:

```text
Component may re-render too often.
```

---

## Production Tip

For derived data:

```text
Use memoized selectors.
```

Example tools:

```text
Reselect
Redux Toolkit createSelector
```

---

## Interview Answer

> `useSelector` allows a React component to read data from the Redux store. I select only the required state slice to avoid unnecessary re-renders. For derived or computed data, I use memoized selectors.

---

# 11. Redux Middleware

## What Is Middleware?

Middleware sits between:

```text
dispatch
and
reducer
```

Flow:

```text
dispatch(action)
   |
middleware
   |
reducer
   |
store update
```

---

## Why Needed?

Reducers must be pure.

So API calls cannot go inside reducer.

Middleware handles:

```text
API calls
logging
analytics
retry
async workflows
```

---

## Interview Answer

> Redux middleware handles side effects between dispatching an action and reaching the reducer. Since reducers must stay pure, middleware is used for API calls, async logic, logging, retries, and analytics.

---

# 12. Thunk, Saga, Fetch, Promise

This confused many beginners, so separate them clearly.

---

## `fetch`

`fetch` is browser API used to make HTTP requests.

```js
fetch("/api/accounts");
```

---

## Promise

Promise represents a value that will arrive later.

```js
fetch("/api/accounts")
  .then(response => response.json())
  .then(data => console.log(data));
```

---

## Thunk

Thunk lets Redux action creator return a function.

Used for simple async work.

```js
function loadAccounts() {
  return async dispatch => {
    dispatch({ type: "ACCOUNTS_LOADING" });

    try {
      const response = await fetch("/api/accounts");
      const data = await response.json();
      dispatch({ type: "ACCOUNTS_SUCCESS", payload: data });
    } catch (error) {
      dispatch({ type: "ACCOUNTS_FAILURE", error: error.message });
    }
  };
}
```

---

## Saga

Saga is middleware for complex async workflows.

Good for:

```text
cancellation
retry
polling
background workflows
complex orchestration
```

Example style:

```js
function* loadAccountsSaga() {
  try {
    const response = yield call(fetch, "/api/accounts");
    const data = yield call([response, "json"]);
    yield put({ type: "ACCOUNTS_SUCCESS", payload: data });
  } catch (error) {
    yield put({ type: "ACCOUNTS_FAILURE", error: error.message });
  }
}
```

---

## Simple Comparison

| Concept | Meaning |
|---|---|
| fetch | Makes HTTP call |
| Promise | Future result |
| Thunk | Simple Redux async function |
| Saga | Advanced async workflow middleware |

---

## Interview Answer

> `fetch` makes HTTP requests and returns a Promise. A Promise represents an async result. Redux Thunk is middleware that allows action creators to return functions for simple async logic. Redux Saga is middleware for more complex async flows like cancellation, retries, polling, and orchestration.

---

# 13. Redux Thunk vs Redux Saga

## Thunk

Use when:

```text
Simple API call
Loading/success/failure
Small to medium app
Easy learning curve
```

---

## Saga

Use when:

```text
Complex async workflow
Cancel previous request
Retry with delay
Listen to many actions
Long-running background process
```

---

## Which One For Interview?

Say:

```text
I would start with Thunk for simple async calls.
I would use Saga only when async flow becomes complex.
```

---

## Interview Answer

> Thunk is simpler and good for normal API calls where we dispatch loading, success, and failure actions. Saga is more powerful for complex workflows like cancellation, retry, polling, and sequencing multiple actions. I would not add Saga unless the complexity justifies it.

---

# 14. HTTP Method For Update

## Common HTTP Methods

```text
GET    -> read
POST   -> create or submit action
PUT    -> replace full resource
PATCH  -> partial update
DELETE -> delete
```

---

## PUT Example

Replace full user:

```http
PUT /users/101
```

Body:

```json
{
  "name": "John",
  "email": "john@example.com"
}
```

---

## PATCH Example

Update only email:

```http
PATCH /users/101
```

Body:

```json
{
  "email": "new@example.com"
}
```

---

## Interview Answer

> For updates, `PUT` is used when replacing the full resource, while `PATCH` is used for partial updates. `POST` is usually used for creating resources or non-idempotent actions. In REST APIs, method choice should be consistent and documented.

---

# 15. Validate JSON Payload

## What Is Payload?

Payload means request body data.

Example:

```json
{
  "name": "John",
  "amount": 1000
}
```

---

## Frontend Validation

React can check:

```text
required fields
email format
amount greater than zero
max length
disabled submit button
```

---

## Backend Validation

Backend must still validate.

Why?

```text
User can bypass frontend
API can be called from Postman
Other services can call API
Security depends on backend validation
```

---

## Interview Answer

> JSON payload validation should happen on both frontend and backend. Frontend validation improves user experience, but backend validation is mandatory for security and correctness. The backend should validate required fields, data types, ranges, and business rules.

---

# 16. API Backward Compatibility

## What Is It?

API is backward compatible when old clients still work after backend changes.

---

## Breaking Change

Old response:

```json
{
  "id": 1,
  "name": "John"
}
```

New response:

```json
{
  "userId": 1,
  "fullName": "John"
}
```

Old React app expects:

```text
id
name
```

It breaks.

---

## Safe Change

```json
{
  "id": 1,
  "name": "John",
  "userId": 1,
  "fullName": "John"
}
```

Old app still works.

New app can use new fields.

---

## Forward Compatibility

Frontend should ignore unknown fields:

```json
{
  "id": 1,
  "name": "John",
  "extraField": "ignore safely"
}
```

---

## Interview Answer

> API backward compatibility means old clients continue working after backend changes. I avoid renaming or removing fields suddenly. I prefer additive changes, keep old fields during migration, version breaking changes, and make frontend tolerant of unknown or optional fields.

---

# 17. Optimize React Performance

## First Rule

Do not optimize blindly.

First measure using:

```text
React DevTools Profiler
browser performance tools
network tab
```

---

## Common Optimizations

```text
Keep state close to where it is used
Avoid unnecessary re-renders
Use React.memo for stable child components
Use useMemo for expensive calculations
Use useCallback for stable callbacks when needed
Virtualize large lists
Lazy load heavy components
Use stable keys
```

---

## Common Mistakes

```text
Using array index as key for changing list
Putting all state at top-level
Overusing useMemo everywhere
Passing new object/function every render to memoized child
Rendering thousands of rows without virtualization
```

---

## Interview Answer

> I optimize React performance by measuring first, then reducing unnecessary renders. I keep state close to where it is used, use stable keys, memoize expensive calculations, use `React.memo` where useful, lazy load large components, and virtualize large lists. I avoid premature optimization.

---

# 18. Senior-Level Summary Answer

If interviewer asks:

```text
Explain React, Redux, API flow.
```

Say:

> React builds UI using components. Components receive props and manage state. When state changes, React re-renders the UI. React can call backend APIs using `fetch` or Axios, receive JSON, and display that data. For small state, I use local state with `useState`. For shared application state, Redux provides a central store. Components dispatch actions, reducers update state immutably, and selectors read data. API calls and side effects should not go inside reducers; they belong in middleware like Thunk or Saga. In production, I handle loading, error, empty states, validation, authentication errors, API compatibility, and performance.

---

# 19. Common Follow-Up Questions

## What is the difference between props and state?

```text
Props come from parent.
State belongs to component and can change.
```

---

## Why reducers must be pure?

Because Redux should be predictable and testable.

Reducers should not:

```text
mutate state
call API
perform side effects
```

---

## Thunk vs Saga?

```text
Thunk -> simple async API calls
Saga  -> complex async workflows
```

---

## What is `useSelector`?

It reads data from Redux store.

---

## Why immutability in Redux?

Because Redux and React rely on object reference changes to detect updates.

---

## PUT vs PATCH?

```text
PUT   -> full replace
PATCH -> partial update
```

---

## Why frontend validation is not enough?

Because users or systems can bypass frontend and call backend directly.

---

## How do you avoid breaking React app when API changes?

```text
Use backward-compatible API changes
Add fields instead of renaming/removing
Handle optional fields safely
Version breaking changes
Use contract testing
```
