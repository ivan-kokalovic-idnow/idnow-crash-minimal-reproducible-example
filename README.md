# idnow-crash-minimal-reproducible-example

This repository is a minimal reproducible demo of an IDnow crash that we are experiencing. 

## The issue
As soon as both the` IDnow SDK` and `@stripe/stripe-react-native` packages are both added into the project, IDnow crashes when attempting to choose the video call option. Removing the Stripe package seems to prevent this issue from occuring.

A clip of the issue:

https://github.com/SowaLabs/idnow-crash-minimal-reproducible-example/assets/112617948/44ec4113-3fc3-4407-950a-34eb30551d95

After this issue is resolved, we can delete this repository.
