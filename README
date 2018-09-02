# 实现思路

## 一、实现右侧字母索引列表

### 1、自定义View，实现右侧字母列表

+ 实现onMeasure()获取设置每一个item的高度和宽度。

+ 实现onDraw()使用画笔画出每一个字母，
  canvas.drawText(text, x, y, paint)，
  确定每个字母的x坐标和基线坐标。

+ 实现onTouchEvent()处理触摸，
  event.getY()/itemHeight得到当前触摸的字母索引，
  写回调，将文字和位置返回，
  重新绘制invalidate()。

  **注意**：如果当前页面有EditText之类的控件，
  调起软键盘后，onMeasure中的高度会变化，字母列表会压缩，
  重写onSizeChanged(int w, int h, int oldw, int oldh)方法，
  取oldh和h的最大值为高度，得到每一个字母的高度。


## 二、RecyclerView列表

&ensp;&ensp;&ensp;&ensp;
继承RecyclerView.ItemDecoration实现悬停的decoration。

### 1、getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)

+ outRect.set(left, top, right, bottom)四个方向上需要为itemView设置padding的值；
+ 通过parent获取position信息，通过position拿到数据里的每个bean里的分类，
因为数据集已经有序，如果与前一个分类不一样，说明是一个新的分类，则需要绘制分类标题；
+ position==0 设置top为一个悬停item的高度；
+ 其它位置和上一个位置的item进行比较，如果首字母不相同，则设置top
(比如常用的设置item间距)。

### 2、onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)

+ 绘制每一个分类标题的背景和内容；
+ onDraw绘制出的内容是在itemView下层，虽然它可以绘制超出getItemOffsets()里的Rect区域，但是超出区域最终不会显示（但被ItemView覆盖的区域会产生OverDraw）；
+ 通过parent获取绘制UI的left和right以及childCount， 遍历childView，根据childView的position，和getItemOffsets中的判断方法一样，
来决定是否绘制分类Title区域：根据确定的上下左右范围先drawRect绘制一个背景，然后drawText绘制文字；
+ canvas.drawRect(left, top, right, bottom, paint)；
+ canvas.drawText(text, x, y, paint)此处x坐标可为x=child.getPaddingLeft()，获取的是itemView父布局中的paddingLeft属性。

### 3、onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)

+ 实现分类标题的悬停效果；
+ onDrawOver绘制出的内容是在RecyclerView的最上层，会遮挡住ItemView；
+ 首先通过parent获取LayoutManager（由于悬停分组列表的特殊性，写死了是LinearLayoutManger），然后获取当前第一个可见itemView以及position，以及它所属的分类title（tag），然后绘制悬停View的背景和文字；
+ 根据第一个可见item的child.getHeight()+child.getTop()<悬停title的高度，将canvas上移，实现将上一个title上顶的动画效果。

   **注意**：ItemDecoration的onDraw最先调用，绘制在最底层，其上再绘制ItemView 中间层，再上调用ItemDecoration的onDrawOver，绘制在最上层。