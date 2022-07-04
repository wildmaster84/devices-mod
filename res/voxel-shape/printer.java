Stream.of(
Block.box(2, 0, 4, 14, 5, 9),
Block.box(3.5, 0.1, 9, 12.5, 1.1, 15),
Block.box(1, 0, 2, 4, 5, 4),
Block.box(1, 0, 9, 4, 3, 11),
Block.box(12, 0, 9, 15, 3, 11),
Block.box(12, 0, 2, 15, 5, 4),
Block.box(1.1, 0, 4, 14.9, 5, 9),
Block.box(4, 0, 2, 12, 3, 4),
Block.box(3.5, 0.1, 15, 12.5, 1.1, 8.5),
Block.box(1.01, 3, 9, 14.99, 5, 11),
Block.box(4, 3, 0, 12, 9.3, 3.4)
).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();