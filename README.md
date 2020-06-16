# serverless-ide-plugins
Jetbrains Plugin to add Navigation/Completion/Syntax on SERVERLESS files

- Navigation within serverless.yml file (find resources): `${self:xxx.yyy.zzz}`. Go directly to `xxx.yyy.zzz` defined in the file of in another Yaml file.
![alt text](./docs/jump.to.definition.png "Jump to value definition")


- Navigation from serverless.yml to imported files: `$file(...)`
![alt text](./docs/jump.to.file.png "Jump to file imported")

- Navigation from serverless.yml lambda definitions to code (TS, JS, PY)
![alt text](./docs/jump.to.code.png "Jump to lambda code implementation")

- Navigation from step functions to lambda definitions
![alt text](./docs/jump.to.lambda.png "Jump to lambda definition")

- Navigation from state machine "step" usage to "step" definition

- Code completion for AWS serverless file
![alt text](./docs/completion.png "AWS Completion")

- Syntax highlighting of errors: step not found
![alt text](./docs/syntax.step.not.found.png "Step not found")

- Syntax highlighting of errors: step unused (dead code)
![alt text](./docs/syntax.step.unused.png "Step unused")

- Syntax highlighting of errors: lambda code file not found
![alt text](./docs/syntax.lambda.code.not.found.png "Lambda Code not found")