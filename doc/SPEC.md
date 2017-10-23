# DOLPHIN PLATFORM SPEC

**Revisions:**

- v0.1 / 2017-10-23 (Benjamin Gudehus, Early Draft)

## About

This is an unofficial specification for Dolphin Platform. It documents the structures that are transmitted and received.

## Commands and Notifications

**CreateContext:**

Seen as request.

~~~ts
{
  id: "CreateContext"
}
~~~

**DestroyContext:**

_TBD_.

**CreateController:**

Seen as request.

~~~ts
{
  id: "CreateController",
  c_id: String|null, // controller id
  n: String // name
}
~~~

**DestroyController:**

Seen as request.

~~~ts
{
  id: "DestroyController",
  c_id: String // controller id
}
~~~

**CreatePresentationalModel:**

Seen as request and response.

~~~ts
{
  id: "CreatePresentationalModel",
  p_id: String, // presentational model id
  t: String, // type
  a: { // attributes
    a_id: String, // attribute id
    n: String, // name
    v: any|null // value
  }[]
}
~~~

**DestroyPresentationalModelCommand:**

Seen as response.

~~~ts
{
  id: "DeletePresentationalModelCommand",
  p_id: String // presentational model id
}
~~~

**ValueChanged:**

Seen as response.

~~~ts
{
  id: "ValueChanged",
  a_id: String, // attribute id
  v: any|null // value
}
~~~

**CallAction:**

_TDB_.

**StartLongPoll:**

Seen as request.

~~~ts
{
  id: "StartLongPoll"
}
~~~

**InterruptLongPoll:**

Seen as request.

~~~ts
{
  id: "InterruptLongPoll"
}
