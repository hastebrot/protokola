# DOLPHIN PLATFORM SPEC

**Revisions:**

- v0.1 / 2017-10-23 (Benjamin Gudehus, Early Draft)

**Table of Contents:**

<!-- TOC depthFrom:2 orderedList:true -->

1. [About](#about)
2. [Commands and Notifications](#commands-and-notifications)
    1. [CreateContext](#createcontext)
    2. [DestroyContext](#destroycontext)
    3. [CreateController](#createcontroller)
    4. [DestroyController](#destroycontroller)
    5. [CreatePresentationalModel](#createpresentationalmodel)
    6. [DestroyPresentationalModelCommand](#destroypresentationalmodelcommand)
    7. [ValueChanged](#valuechanged)
    8. [CallAction](#callaction)
    9. [StartLongPoll](#startlongpoll)
    10. [InterruptLongPoll](#interruptlongpoll)

<!-- /TOC -->

## About

This is an internal specification for Dolphin Platform. It documents the structures that are transmitted and received.

## Commands and Notifications

### CreateContext

Seen as request.

~~~ts
{
  id: "CreateContext"
}
~~~

### DestroyContext

_TBD_.

### CreateController

Seen as request.

~~~ts
{
  id: "CreateController",
  c_id: String|null, // controller id
  n: String // name
}
~~~

### DestroyController

Seen as request.

~~~ts
{
  id: "DestroyController",
  c_id: String // controller id
}
~~~

### CreatePresentationalModel

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

### DestroyPresentationalModelCommand

Seen as response.

~~~ts
{
  id: "DeletePresentationalModelCommand",
  p_id: String // presentational model id
}
~~~

### ValueChanged

Seen as response.

~~~ts
{
  id: "ValueChanged",
  a_id: String, // attribute id
  v: any|null // value
}
~~~

### CallAction

_TDB_.

### StartLongPoll

Seen as request.

~~~ts
{
  id: "StartLongPoll"
}
~~~

### InterruptLongPoll

Seen as request.

~~~ts
{
  id: "InterruptLongPoll"
}
