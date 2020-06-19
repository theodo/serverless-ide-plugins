# serverless-ide-plugins
Add Navigation/Completion/Syntax on SERVERLESS files

<h3>Navigations</h3>

- Navigation within serverless.yml file (find resources): `${self:xxx.yyy.zzz}`<br> 
Go directly to `xxx.yyy.zzz` defined in the file or in another Yaml file.
If the tag `xxx.yyy.zzz` is not found, search the longest matching sub tag if exist (`xxx.yyy` then `xxx`)
![alt text](./docs/jump.to.definition.png "Jump to value definition")

- Navigation from serverless.yml to imported files: `$file(./directory/an_included_file.yml)`<br>
![alt text](./docs/jump.to.file.png "Jump to file imported")

- Navigation from serverless.yml lambda definitions to code (TS, JS, PY)<br>
Search for `handler` tag located under `functions` tag. Then open the associated code file.
![alt text](./docs/jump.to.code.png "Jump to lambda code implementation")

- Navigation from a given step in state machine to lambda definition (found in any Yaml file)<br>
Search for `Fn::GetAtt: [XXXX, Arn]` located inside Step in State Machine definition. <br>
Then jump to `XXXX` definition found in serverless files.
![alt text](./docs/jump.to.lambda.png "Jump to lambda definition")

- Navigation from state machine "step" usage to "step" definition<br>
Search for `Next: XXXX` or `Default: XXXX`tags in state machine definition.<br>
Then jump to `XXXX` step found in the current file (if exists)

<h3>Code Completion</h3>

- Code completion for AWS serverless file<br>
Relies on https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/serverless types definition.<br>
Provides Completion for AWS provider tags.
![alt text](./docs/completion.png "AWS Completion")

<h3>Syntax Inspections</h3>

- Syntax highlighting of errors: step not found<br>
Search for Steps used in a given State Machine (within `Next` or `Default` tags), not found in the file.
![alt text](./docs/syntax.step.not.found.png "Step not found")

- Syntax highlighting of errors: step unused (dead code)<br>
Search for Steps defined in a given State Machine not used within `Next` or `Default` tags. Unreachable code
![alt text](./docs/syntax.step.unused.png "Step unused")

- Syntax highlighting of errors: lambda code file not found<br>
Search for lambda definition with erroneous associated code file 
![alt text](./docs/syntax.lambda.code.not.found.png "Lambda Code not found")

- Syntax highlighting of errors: an included file not found<br>
Search for include tags leading to non existing included file.
![alt text](./docs/syntax.included.file.not.found.png "Lambda Code not found")

XXXX