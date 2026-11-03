import bpy,os
from mathutils import Vector
bpy.ops.wm.open_mainfile(filepath=os.path.abspath('assets-source/coins/coin.blend'))
o=bpy.data.objects['Coin.tris']
bpy.context.scene.frame_set(1)
o.animation_data_clear()
matrix=o.matrix_world.copy();o.parent=None;o.matrix_world=matrix
for mod in list(o.modifiers): o.modifiers.remove(mod)
for other in list(bpy.data.objects):
 if other!=o:bpy.data.objects.remove(other,do_unlink=True)
bpy.context.view_layer.objects.active=o;o.select_set(True)
bpy.ops.object.transform_apply(location=False,rotation=True,scale=True)
center=sum((Vector(v) for v in o.bound_box),Vector())/8
for v in o.data.vertices:v.co-=center
size=max(o.dimensions)
o.scale=(.044/size,)*3;o.location=(0,0,0)
mat=bpy.data.materials.new('Original coin colour');mat.use_nodes=True
bs=mat.node_tree.nodes.get('Principled BSDF');bs.inputs['Metallic'].default_value=.45;bs.inputs['Roughness'].default_value=.3
tex=mat.node_tree.nodes.new('ShaderNodeTexImage');tex.image=bpy.data.images.load(os.path.abspath('.scratch/coins/coin_textures/coinC_2.png'))
mat.node_tree.links.new(tex.outputs['Color'],bs.inputs['Base Color'])
o.data.materials.clear();o.data.materials.append(mat)
o.data.validate();o.data.update()
bpy.ops.export_scene.gltf(filepath=os.path.abspath('app/src/main/assets/models/coin.glb'),export_format='GLB',use_selection=True,export_animations=False)
