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

- Syntax highlighting of errors
