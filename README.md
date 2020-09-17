#字节码插桩
##看代码的src/test/java部分
- 利用Android studio的插件ASM Bytecode Viewer
- 右击需要插桩的文件，选择ASM Bytecode Viewer，会生成字节码文件
- 我这里实在单元测试中完成的，如果在使用选择ASM Bytecode Viewer时报错，将对应的文件放入main下面再尝试